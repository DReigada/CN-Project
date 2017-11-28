package part1


import helpers.Helpers

import scala.annotation.tailrec
import scala.collection.mutable
import scala.util.Try
import scala.util.matching.Regex

object ChangeNameForId extends App with Helpers {

  val subsWithIDFile = args(0)
  val usersWithIDFile = args(1)
  val fileToChange = args(2)
  val outputFile = args(3)
  val drop = Try(args(4)).toOption.fold(0)(_.toInt)

  val subsRegex: Regex = """(\d*),(.*)""".r
  val fileToChangeRegex: Regex = """(.*),(.*),(\d*)""".r

  val subsMap = getMapFromFile(subsWithIDFile, { case subsRegex(id, subName) => subName -> id.toLong })
  println(s"Done importing subs Ids. Size: ${subsMap.size}")

  val usersMap = getMapFromFile(usersWithIDFile, { case subsRegex(id, subName) => subName -> id.toLong })
  println(s"Done importing users Ids. Size: ${usersMap.size}")

  val outputStream = outputStreamFromFile(outputFile)
  writeStreamTo(stream.drop(drop), outputStream)
  outputStream.close()

  private def stream = streamFromFile(fileToChange)
    .collect {
      case fileToChangeRegex(userR, subR, countR) => (usersMap(userR), subsMap(subR), countR.toLong)
    }
    .map((toCsv _).tupled)

  // The map is mutable for efficiency reasons
  private def getMapFromFile(file: String, f: PartialFunction[(String), (String, Long)]): mutable.Map[String, Long] = {
    val map = mutable.HashMap.empty[String, Long]
    consume(streamFromFile(file).collect(f), map)
    map
  }

  @tailrec
  private def consume(xs: Stream[(String, Long)], map: mutable.Map[String, Long], i: Long = 0): mutable.Map[String, Long] = {
    if (xs.isEmpty) {
      map
    } else {
      if (i % 100000 == 0) { // this is just to track the progress
        println(i)
      }
      map.+=(xs.head)
      consume(xs.tail, map, i + 1)
    }
  }


  private def toCsv(user: Long, sub: Long, weight: Long): String = {
    s"$user,$sub,$weight\n"
  }


}
