import java.io.{BufferedOutputStream, FileOutputStream, OutputStream}

import scala.annotation.tailrec
import scala.io.Source

trait Helpers {


  implicit class StreamChopOps[T](xs: Stream[T]) {
    def chopBy[U](f: T => U): Stream[Stream[T]] = xs match {
      case x #:: _ =>
        def eq(e: T) = f(e) == f(x)

        xs.takeWhile(eq) #:: xs.dropWhile(eq).chopBy(f)
      case _ => Stream.empty
    }
  }

  def outputStreamFromFile(file: String): OutputStream = {
    new BufferedOutputStream(new FileOutputStream(file))
  }

  def streamFromFile(file: String): Stream[String] = {
    Source.fromFile(file).getLines().toStream
  }

  @tailrec
  final def writeStreamTo(xs: Stream[String], output: OutputStream): Unit = {
    if (xs.isEmpty) {
      ()
    } else {
      output.write(xs.head.getBytes())
      writeStreamTo(xs.tail, output)
    }
  }
}
