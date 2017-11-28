package part2

import helpers.Helpers

import scala.io.Source

object ParseAssocRulesConsequentSize extends App with Helpers {

  val inputFile = args(0)
  val mapFile = args(1)
  val outputFile = args(2)

  val regex = """\[(.*)\] => \[(.*)\], (.*)""".r

  val names = new SubsNamesMap(mapFile)

  val outputStream = outputStreamFromFile(outputFile)
  val write = writeToStream(outputStream)(_)


  Source.fromFile(inputFile).getLines().toStream
    .map {
      case regex(anteStr, conseqStr, confidenceStr) =>
        (anteStr, conseqStr, confidenceStr)
    }
    .groupBy(_._2)
    .map {
      case (conseq, stream) => (names.replaceByName(conseq), stream.size)
    }(collection.breakOut)
    .sortBy(_._2)
    .foreach {
      case (conseq, size) =>
        write(s"$conseq => $size\n")
    }

  outputStream.close()

}
