package infrastructure

import java.io.File
import java.nio.file.Paths

package object files {

  def listFiles(path: String): List[File] = {
    val root = Paths
      .get(path)
      .toAbsolutePath
      .toFile

    val entries = if (root.isDirectory) {
      listFilesInternal(root)
        .filterNot(_.isDirectory)
        .toList
    } else {
      List(root)
    }
    entries
  }

  private def listFilesInternal(f: File): Array[File] = {
    val these = f.listFiles
    these ++ these.filter(_.isDirectory).flatMap(listFilesInternal)
  }

}
