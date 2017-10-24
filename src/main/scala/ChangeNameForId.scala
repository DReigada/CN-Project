
import scala.util.matching.Regex

object ChangeNameForId extends App with Helpers {

  val subsWithIDFile = args(0)
  val usersWithIDFile = args(1)
  val fileToChange = args(2)
  val outputFile = args(3)

  // Consistency FTW <3
  val subsRegex: Regex =
    """(.*),(\d*)""".r
  val usersRegex: Regex = """(\d*),(.*)""".r
  val fileToChangeRegex: Regex = """(.*),(.*),(\d*)""".r

  val subsMap = mapFromFile(subsWithIDFile, { case subsRegex(subName, id) => subName -> id.toLong })
  val usersMap = mapFromFile(usersWithIDFile, { case usersRegex(id, subName) => subName -> id.toLong })

  println("Done importing Ids")

  def stream = streamFromFile(fileToChange)
    .collect {
      case fileToChangeRegex(userR, subR, countR) if userR != "--tech" => (usersMap(userR), subsMap(subR), countR.toLong)
    }
    .map((toCsv _).tupled)

  val outputStream = outputStreamFromFile(outputFile)
  writeStreamTo(stream, outputStream)
  outputStream.close()

  def mapFromFile(file: String, f: PartialFunction[(String), (String, Long)]) = {
    streamFromFile(file)
      .collect(f)
      .foldLeft(Map.empty[String, Long]) {
        case (map, tuple) => map.+(tuple)
      }
  }


  def toCsv(user: Long, sub: Long, weight: Long): String = {
    s"$user,$sub,$weight\n"
  }


}
