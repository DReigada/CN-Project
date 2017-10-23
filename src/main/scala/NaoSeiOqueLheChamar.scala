import java.io.{BufferedOutputStream, FileOutputStream}

import scala.annotation.tailrec
import scala.io.Source
import scala.util.Try
import scala.util.matching.Regex


object NaoSeiOqueLheChamar extends App {

  val regex: Regex = """(.*),(.*),(\d*)""".r

  val inputFile = args(0)
  val outputFile = args(1)
  val lines: Stream[String] = Source.fromFile(inputFile).getLines().toStream


  val target = new BufferedOutputStream(new FileOutputStream(outputFile))

  sys.addShutdownHook(target.close())


  def stream = Source.fromFile(inputFile)
    .getLines().toStream.map(Line.apply)
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

  consume(stream)

  @tailrec
  def consume(xs: Stream[String]): Unit = {
    if (xs.isEmpty) {
      ()
    } else {
      target.write(xs.head.getBytes())
      consume(xs.tail)
    }
  }

  target.close()

  //  attempt.get

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

  implicit class StreamChopOps[T](xs: Stream[T]) {
    def chopBy[U](f: T => U): Stream[Stream[T]] = xs match {
      case x #:: _ =>
        def eq(e: T) = f(e) == f(x)

        xs.takeWhile(eq) #:: xs.dropWhile(eq).chopBy(f)
      case _ => Stream.empty
    }
  }

}

