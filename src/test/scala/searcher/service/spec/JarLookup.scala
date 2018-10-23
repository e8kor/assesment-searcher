package searcher.service.spec

import java.io.InputStream

import infrastructure.resource.ResourceLookup


case object JarLookup extends ResourceLookup[String] {

  override def getInputStream(arg: String): InputStream = {
    getClass.getResourceAsStream(arg)
  }

  override def name(arg: String): String = arg

}