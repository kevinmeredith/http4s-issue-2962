package net

import java.util.concurrent.atomic.AtomicInteger
import cats.effect.{IO, Timer}
import fs2.Stream
import org.http4s.Request
import org.http4s._
import org.http4s.client.blaze.BlazeClientBuilder
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext

object Test {

  // Note - I copied this code from https://github.com/http4s/http4s/issues/2962#issue-518017553
  def main(args: Array[String]): Unit = {
    val int                       = new AtomicInteger(0)
    implicit val CS               = IO.contextShift(ExecutionContext.global)
    implicit val timer: Timer[IO] = IO.timer(ExecutionContext.global)
    val timeout                   = 1 seconds

    val program = for {
      client <- BlazeClientBuilder[IO](scala.concurrent.ExecutionContext.global)
        .withRequestTimeout(timeout)
        .withResponseHeaderTimeout(timeout)
        .allocated
      c      = client._1
      status = c.status(Request[IO](uri = uri"""http://httpbin.org/status/500""")).attempt
      _ <- Stream(Stream.eval(status)).repeat
        .covary[IO]
        .parJoin(200)
        .take(1000)
        .flatMap(y => Stream.eval(IO(println(">>> " + int.incrementAndGet + " " + y))))
        .compile
        .drain
      s <- c.status(Request[IO](uri = uri"""http://httpbin.org/status/500""")).attempt
      _ <- IO(println("STATUS = " + s.right.get))
    } yield ()

    program.unsafeRunSync()
  }
}