package part2

import helpers.Helpers

import scala.io.Source

object ParseAssocRulesReplacingNames extends App with Helpers {

  val inputFile = args(0)
  val mapFile = args(1)
  val outputFile = args(2)

  val regex = """\[(.*)\] => \[(.*)\], (.*)""".r

  val names = new SubsNamesMap(mapFile)

  val outputStream = outputStreamFromFile(outputFile)
  val write = writeToStream(outputStream)(_)


  Source.fromFile(inputFile).getLines().toStream
    .map {
      case regex(anteStr, conseqStr, confidenceStr) => (anteStr, conseqStr, confidenceStr)
    }
    .map {
      case (anteStr, conseqStr, confidenceStr) =>
        val antecedents =
          names
            .replaceByNames(anteStr.split(","))

        val consequents =
          names
            .replaceByNames(conseqStr.split(","))

        (antecedents, consequents, confidenceStr)
    }
    .sortBy(_._1.size)
    .foreach {
      case (antecedents, consequents, confidenceStr) =>
        val antecedentsStr = antecedents.mkString("[", ", ", "]")
        val consequentsStr = consequents.mkString("[", ", ", "]")

        write(s"$antecedentsStr => $consequentsStr, $confidenceStr\n")
    }

  outputStream.close()

}
