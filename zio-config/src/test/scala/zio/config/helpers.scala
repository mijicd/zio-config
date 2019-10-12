package zio.config

import zio.config.Config._
import zio.random.Random
import zio.test.Gen

object helpers {
  final case class Id(value: String) extends AnyVal

  final case class OverallConfig(id1: Option[Id], id2: Id)

  val key: (Int, Int) => String = (n1, n2) => s"GROUP${n1}_id_${n2}"

  val cId: String => ConfigDescriptor[Id] = string(_).xmap(Id)(_.value)

  val genId =
    for {
      n <- Gen.int(1, 5)
      s <- Gen.listOfN(n)(Gen.alphaNumericChar)
    } yield Id(s.mkString)

  val genOverallConfig: Gen[Random, Map[String, String]] =
    for {
      optId1 <- Gen.option(genId)
      id2    <- genId
      n      <- Gen.oneOf(Gen.const(1), Gen.const(10), Gen.const(100))
    } yield (0 to n).flatMap { nn =>
      List(key(nn, 2) -> id2.value) ++ optId1.toList.flatMap(id1 => List(key(nn, 1) -> id1.value))
    }.toMap
}
