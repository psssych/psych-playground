package pl.psych.playground

import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._
import fs2.{io, text}
import java.nio.file.Paths
import java.util.concurrent.Executors

import scala.concurrent.{ExecutionContext, Future}

object Converter extends IOApp {
  def run(args: List[String]): IO[ExitCode] = {
    def fahrenheitToCelsius(f: Double): Double =
      (f - 32.0) * (5.0/9.0)

    val blockingExecutionContext = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(2))

    io.file.readAll[IO](Paths.get("testdata/fahrenheit.txt"), blockingExecutionContext, 4096)
      .through(text.utf8Decode)
      .through(text.lines)
      .filter(s => !s.trim.isEmpty && !s.startsWith("//"))
      .map(line => fahrenheitToCelsius(line.toDouble).toString)
      .intersperse("\n")
      .through(text.utf8Encode)
      .through(io.file.writeAll(Paths.get("testdata/celsius.txt"), blockingExecutionContext))
      .compile
      .drain
      .as(ExitCode.Success)
  }
}

object Example extends IOApp {

  def loadUserIdByName(userName: String): Future[Long] = ???

  def run(args: List[String]): IO[ExitCode] = {
    ???
  }
}