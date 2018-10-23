package service

import scala.io._
import infrastructure._
import infrastructure.resource.ResourceLookup

import scala.util._

trait Cache {

  def calculate(keywords: Seq[Token]): List[Statistic]

}

object Cache {

  def apply[A](
    limit: Int,
    args: Seq[A]
  )(
    implicit ordering: Ordering[Statistic],
    lookup: ResourceLookup[A]
  ): Either[String, CachedFile] = {

    import infrastructure.resource._
    Try {
      val entries = for {
        arg <- args.seq
      } yield {
        withResources(Source.fromInputStream(lookup.getInputStream(arg))) { is =>
          is.getLines()
            .flatMap(_.split(delimiter))
            .map(_.replaceAll(punctuation, ""))
            .map(x => x -> Set(lookup.name(arg)))
            .toMap
        }
      }

      val merged = merge(entries)
      new CachedFile(merged, limit)
    } match {
      case Failure(ex) =>
        ex.printStackTrace()
        Left(s"error while preparing cache at path ${args.map(lookup.name).mkString(", ")}: ${ex.getMessage}")
      case Success(items) =>
        Right(items)
    }

  }

  private def merge(
    entries: Seq[Map[Token, Set[Filename]]],
    empty: Map[Token, Set[Filename]] = Map.empty
  ): Map[Token, Set[Filename]] = {
    val merged: Map[Token, Set[Filename]] = entries.foldLeft(empty)((r, m) => m.foldLeft(r) {
      case (dict, (k, v)) => dict + (k -> (v ++ dict.getOrElse(k, Nil)))
    })
    merged
  }

}

class CachedFile(
  val mapping: Map[Token, Set[Filename]],
  val limit: Int
)(implicit ordering: Ordering[Statistic]) extends Cache {

  def calculate(keywords: Seq[Token]): List[Statistic] = {
    val part: Float = 100F / keywords.size
    val keys = keywords
    val empty = Map.empty[String, Float]

    val stats = keys.foldLeft(empty) {
      case (accum, keyword) if mapping.contains(keyword) =>
        val filenames = mapping(keyword)
        filenames.foldLeft(accum) {
          case (accum, filename) if accum.contains(filename) =>
            accum.updated(filename, accum(filename) + part)
          case (accum, filename) =>
            accum + (filename -> part)
        }
      case (accum, _) =>
        accum
    }

    val rounded = stats.mapValues(value => Math.round(value))

    val prepared = rounded.filter(_._2 > 0).toList.sorted

    prepared.take(limit)
  }
}

