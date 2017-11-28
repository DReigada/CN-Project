package part2

import scala.io.Source

object CreateDistributionFile extends App {

  val regex = """\[(.*)\], (.*)""".r

  val inputFile = args(0)
  val outputFile = args(1)

  val output = ReplaceNames.outputStreamFromFile(outputFile)

  Source.fromFile(inputFile)
    .getLines()
    .toStream
    .map {
      case regex(idsStr, countStr) =>
        val ids = idsStr.split(",")
        val count = countStr.toInt

        count
    }
    .sorted
    .map(a => s"$a\n")
    .foreach(a => output.write(a.getBytes))

  output.close()

}
