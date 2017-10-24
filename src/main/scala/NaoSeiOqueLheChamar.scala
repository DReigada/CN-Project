import java.io.{BufferedOutputStream, FileOutputStream}

import scala.io.Source
import scala.util.Try
import scala.util.matching.Regex


object NaoSeiOqueLheChamar extends App with Helpers {

  val regex: Regex = """(.*),(.*),(\d*)""".r

  val inputFile = args(0)
  val outputFile = args(1)

  val target = outputStreamFromFile(outputFile)

  sys.addShutdownHook(target.close())


  def stream = streamFromFile(inputFile)
    .map(Line.apply)
    .filter { line =>
      val isValid = Try(line.user.nonEmpty && line.sub.nonEmpty).toOption.exists(identity)
      if (!isValid) {
        println(line)
      }
      isValid
    }
    .chopBy(_.sub)
    .flatMap { subreddit =>
      for {
        user1 <- subreddit
        user2 <- subreddit if user1.user != user2.user
      } yield {
        UserRelation(user1.user, user2.user, math.log(user1.count) + math.log(user2.count))
      }
    }
    .map(_.toCsv)

  writeStreamTo(stream, target)

  target.close()

  case class Line(str: String) {
    lazy val (user, sub, count) = str match {
      case regex(userR, subR, countR) => (userR, subR, countR.toLong)
    }
  }

  case class UserRelation(user1: String, user2: String, weight: Double) {
    def toCsv: String = {
      if (user1 < user2) {
        s"$user1,$user2,$weight\n"
      } else {
        s"$user2,$user1,$weight\n"
      }
    }
  }


}

