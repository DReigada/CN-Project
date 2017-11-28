package part2

import scala.io.Source

class SubsNamesMap(inputFile: String) {

  private val mapLines = Source.fromFile(inputFile).getLines().toStream

  val namesMap: Map[Int, String] = {
    mapLines.foldLeft(Map.empty[Int, String]) {
      case (map, line) =>
        val split = line.split(",")

        val id = split(0).toInt

        if (split.length > 1) {
          map + (id -> split(1))
        } else if (id == 1822) {
          map + (id -> "null")
        } else {
          map
        }
    }
  }

  def replaceByNames(ids: Array[String]): Seq[String] = {
    ids.map(id => namesMap(id.toInt))(collection.breakOut)
  }

  def replaceByName(id: String): String = namesMap(id.toInt)

}
