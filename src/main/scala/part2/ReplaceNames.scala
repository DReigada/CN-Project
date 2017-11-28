package part2

import helpers.Helpers

import scala.io.Source

object ReplaceNames extends App with Helpers {

  val file = args(0)
  val mapFile = args(1)
  val outFile = args(2)

  val regex = """\[(.*)\], (.*)""".r


  val output = outputStreamFromFile(outFile)


  val lines = Source.fromFile(file).getLines().toStream

  val namesMap = new SubsNamesMap(mapFile).namesMap


  val t = lines
    .map {
      case regex(subs, count) => (subs, count)
    }
    .map { case (subsArr, count) =>
      val subs = subsArr.split(",")

      val names =
        subs
          .map(_.toInt)
          .map(namesMap)
          .mkString("[", ", ", "]")


      (names, count.toInt, subs.length)
    }
    .sortBy(_._2)
    .map { case (names, count, _) => s"$names, $count\n" }

  t.foreach(a => output.write(a.getBytes))

  output.close()

}
