import scala.util.matching.Regex


object CreateUserBuckets extends App with Helpers {

  val regex: Regex = """(\d*),(\d*),(\d*)""".r

  val inputFile = args(0)
  val outputFile = args(1)

  val target = outputStreamFromFile(outputFile)

  sys.addShutdownHook(target.close())
  println("starting2")

  var userCount = 0

  def stream = streamFromFile(inputFile)
    .map(Line.apply)
    .filter { line => line.user != 6335974L } // filter [deleted] user, TODO: this should not be hardcoded

  chopByWithValue(stream)(_.user) { (user, lines) =>
    userCount += 1
    if (userCount % 10000 == 0) { // this is just to track the progress
      println(userCount)
    }

    // buckets with only one item don't really matter
    if(lines.size > 1) {
      val userSubs = lines.map(_.sub).mkString(" ")

      val newLine = s"$user: $userSubs\n"
      writeStreamTo(Seq(newLine), target)
    }
  }

  target.close()

  case class Line(str: String) {
    lazy val (user, sub, count) = str match {
      case regex(userR, subR, countR) => (userR.toLong, subR.toLong, countR.toLong)
    }
  }

}

