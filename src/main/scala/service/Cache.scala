package service

import java.io._
import java.nio.file.Paths

import scala.annotation.tailrec
import scala.util._
import scala.io._

import infrastructure._

trait Cache {

  def calculate(keywords: Seq[Token]): List[Statistic]

}

object Cache {

  def apply(limit: Int, path: String, trimCommonPrefix: Boolean)(implicit codec: Codec, ordering: Ordering[Statistic]): Either[String, Cache] = {
    Try {
      val root = Paths
        .get(path)
        .toAbsolutePath
        .toFile
      val entries = if (root.isDirectory) {
        listFiles(root)
          .filterNot(_.isDirectory)
          .toList
          .map(prepare)
      } else {
        List(prepare(root))
      }
      apply(limit, entries, trimCommonPrefix)
    } match {
      case Failure(ex) =>
        ex.printStackTrace()
        Left(s"error while preparing cache at path $path: ${ex.getMessage}")
      case Success(items) =>
        Right(items)
    }
  }

  def apply(limit: Int, tuple: Seq[FileMeta], trimCommonPrefix: Boolean)(implicit codec: Codec, ordering: Ordering[Statistic]): Cache = {
    val entries = if (trimCommonPrefix) {
      normalize(tuple).map(prepare)
    } else {
      tuple.map(prepare)
    }
    val merged = merge(entries)
    new CachedFile(merged, limit)
  }

  private def listFiles(f: File): Array[File] = {
    val these = f.listFiles
    these ++ these.filter(_.isDirectory).flatMap(listFiles)
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

  private def prepare(file: File): FileMeta = {
    file.getAbsolutePath -> new FileInputStream(file)
  }

  private def normalize(seq: Seq[FileMeta]): Seq[FileMeta] = {

    def prefix(arg: String, root: String): String = arg
      .zip(root)
      .takeWhile { case (a, b) => a == b }
      .map(_._1)
      .mkString

    @tailrec
    def find(xs: Seq[String], root: String): String = xs match {
      case h :: Nil => prefix(h, root)
      case h :: t => find(t, prefix(h, root))
    }

    if (seq.length > 1) {
      val (names, _) = seq.unzip
      val commonPrefix = find(names.tail, names.head)

      val normalized = seq.map {
        case (key, value) =>
          key.replace(commonPrefix, "") -> value
      }

      normalized
    } else {
      seq
    }

  }

  private def prepare(tuple: FileMeta)(implicit codec: Codec): Map[Token, Set[Filename]] = {
    val (name, is) = tuple
    val names = Set(name)
    val cache: Map[Token, Set[Filename]] = Source.fromInputStream(is)
      .getLines()
      .flatMap(_.split(delimiter))
      .map(_.replaceAll(punctuation, ""))
      .map(x => x -> names)
      .toMap

    cache
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

