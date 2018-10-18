import java.io.InputStream

package object service {

  type Token = String

  type Filename = String

  type Statistic = (String, Int)

  type FileMeta = (String, InputStream)

}
