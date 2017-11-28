package helpers

import java.io.{BufferedOutputStream, FileOutputStream, OutputStream}

import scala.annotation.tailrec
import scala.io.Source

trait Helpers {
  @tailrec
  final def chopBy[T, U](xs: Stream[T])(f: T => U)(app: Stream[T] => Unit): Unit = xs match {
    case x #:: _ =>
      def eq(e: T) = f(e) == f(x)

      app(xs.takeWhile(eq))

      chopBy(xs.dropWhile(eq))(f)(app)
    case _ => ()
  }

  @tailrec
  final def chopByWithValue[T, U](xs: Stream[T])(f: T => U)(app: (U, Stream[T]) => Unit): Unit = xs match {
    case x #:: _ =>
      def eq(e: T) = f(e) == f(x)

      app(f(x), xs.takeWhile(eq))

      chopByWithValue(xs.dropWhile(eq))(f)(app)
    case _ => ()
  }


  def outputStreamFromFile(file: String): OutputStream = {
    new BufferedOutputStream(new FileOutputStream(file))
  }

  def streamFromFile(file: String): Stream[String] = {
    Source.fromFile(file).getLines().toStream
  }

  def writeToStream(output: OutputStream)(line: String): Unit = {
    output.write(line.getBytes())
  }

  @tailrec
  final def writeStreamTo(xs: Seq[String], output: OutputStream): Unit = {
    if (xs.isEmpty) {
      ()
    } else {
      output.write(xs.head.getBytes())
      writeStreamTo(xs.tail, output)
    }
  }
}
