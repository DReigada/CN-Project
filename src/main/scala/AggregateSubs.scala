import scala.util.matching.Regex


object AggregateSubs extends App with Helpers {

  val regex: Regex = """(\d*),(\d*),([+-]?[0-9]*[.]?[0-9]+)""".r

  val inputFile = args(0)
  val outputFile = args(1)

  val target = outputStreamFromFile(outputFile)

  sys.addShutdownHook(target.close())
  println("starting2")

  
  def stream = streamFromFile(inputFile)
    .map(Line.apply)

  chopBy(stream)(a => (a.sub1, a.sub2)) { subreddit =>

    subreddit.map(_.weight).max
    val line = subreddit.head

    val t = Seq(SubredditRelation(line.sub1, line.sub2, subreddit.map(_.weight).max).toCsv)
    writeStreamTo(t, target)
  }

  target.close()

  case class Line(str: String) {
    lazy val (sub1, sub2, weight) = str match {
      case regex(sub1R, sub2R, weightR) => (sub1R.toLong, sub2R.toLong, weightR.toDouble)
    }
  }


  case class SubredditRelation(sub1: Long, sub2: Long, weight: Double) {
    def toCsv: String = {
      if (sub1 < sub2) {
        s"$sub1,$sub2,$weight\n"
      } else {
        s"$sub2,$sub1,$weight\n"
      }
    }
  }


}

