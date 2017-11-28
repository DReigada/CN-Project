package part1

import helpers.Helpers

import scala.util.matching.Regex


object SubToSubEdgesGenerator extends App with Helpers {

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

  chopBy(stream)(_.user) { subreddit =>
    userCount += 1
    if (userCount % 10000 == 0) { // this is just to track the progress
      println(userCount)
    }

    def t = (for {
      user1 <- subreddit
      user2 <- subreddit if user1.sub != user2.sub
    } yield {
      SubredditRelation(user1.sub, user2.sub, math.log(user1.count) + math.log(user2.count))
    }).map(_.toCsv)

    writeStreamTo(t, target)
  }

  target.close()

  case class Line(str: String) {
    lazy val (user, sub, count) = str match {
      case regex(userR, subR, countR) => (userR.toLong, subR.toLong, countR.toLong)
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

