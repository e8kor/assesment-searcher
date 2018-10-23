package infrastructure.resource

import java.io.{File, FileInputStream, InputStream}

trait ResourceLookup[A] {

  def getInputStream(arg: A): InputStream

  def name(arg: A): String

}

case object FileLookup extends ResourceLookup[File] {

  override def getInputStream(arg: File): InputStream = {

    new FileInputStream(arg)
  }

  override def name(arg: File): String = arg.getAbsolutePath
}


