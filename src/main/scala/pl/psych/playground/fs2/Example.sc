
import fs2._

val interspaced: List[String] =  Stream("1","2","3","4").intersperse("\n").toList
val zipped: List[(Int, Option[Int])] = Stream(1,2,3,4).zipWithNext.toList

// SOme blocking interaction with the outside world
import scala.concurrent.Future
import scala.util.Random
def loadUserIdByName(userName: String): Future[Long] = {
  Thread.sleep((Random.nextDouble() * 100).toInt)
  Future.successful(Random.nextInt)
}
// import java.util.concurrent.TimeoutException
//if (Random.nextDouble() < 0.5) {
//  Future.failed(new TimeoutException)
//} else {
//  Thread.sleep((Random.nextDouble() * 100).toInt)
//  Future.successful(userName.toInt)
//}


// a Pure Stream - has literally no effect on the world
val names: Stream[Pure, String] = Stream("bob", "alice", "joe")

// this is still Pure
val stillPure: Stream[Pure, Future[Long]] = names.map(loadUserIdByName)

// this has future ! It isolated the effect - Future (separation of concerns)
val userIdsFromDB: Stream[Future, Long] = names.evalMap(loadUserIdByName)

// To get to the values of the computation we need to `compile` the stream
//userIdsFromDB.compile.toList

// FUTURE is a bad effect type ! Its eager

import cats.effect.IO

val userIds: IO[List[Long]] =
  Stream("bob", "alice", "joe")
    .evalMap(name => IO.fromFuture(IO(loadUserIdByName(name))))
    .compile
    .toList

userIds.unsafeRunSync()