package edu.dbortnichuk.scala

import edu.dbortnichuk.scala.inheritance.SomeClass

case class Box[V1](v: V1)
case class Container[V2](v: V2)

trait Trait1[T1]{
  def getT: T1
}

abstract class CA[A1, B1](a: A1){
  def get: B1
  def getA = a
}

class C1[A2, B2](a: A2, b: B2) extends CA[A2, B2](a) with Trait1[Int] {
  override def get: B2 = b
  override def getT: Int = 0
}

trait Conv1[C1]{
  cnv =>
  def convertStr(c: C1): String
  def contramap[C2](func: C2 => C1): Conv1[C2] =
    new Conv1[C2] {
      def convertStr(value: C2): String =
        cnv.convertStr(func(value))
    }
}

object Conv1Instances{
  implicit val conv1Int: Conv1[Int] = new Conv1[Int] {
    override def convertStr(i: Int): String = i.toString
  }

  implicit val conv1String: Conv1[String] = new Conv1[String] {
    override def convertStr(s: String): String = s
  }

  implicit def conv1Box[C1](implicit p: Conv1[C1]) = p.contramap[Box[C1]](_.v)

  implicit def conv1Container[C1: Conv1] = implicitly[Conv1[C1]].contramap[Container[C1]](_.v)
}

object Conv1Ops{

  def convertStr[A: Conv1](value: A): String = implicitly[Conv1[A]].convertStr(value)
  //def convertStr[A](value: A)(implicit p: Conv1[A]): String = p.convertStr(value)

}

object Conv1Syntax {

  implicit class Conv1Bridge[A: Conv1](value: A) {
    def convertStr: String = implicitly[Conv1[A]].convertStr(value)

  }

}


object Inheritance extends App {

  import edu.dbortnichuk.scala.Conv1Instances._
  import edu.dbortnichuk.scala.Conv1Syntax._

  val a = 1.0
  val b = "one"
  val box1 = Box("Something")
  val box2 = Box(1)
  val box3 = Box(2.0)
  val container = Container("Object")

  //println(Conv1Ops.convertStr(box))
  println(box2.convertStr)

  val sc = new SomeClass("x", "y")
  sc.x = "x1"
  sc.y
  //sc.z
  println(sc.x)



}


//class Tst2() extends Tst {
//
//  def test(t: Tst) = t.x
//
//}
//
//class Tst1() extends Tst {
//
//  def test(t: Tst) = t.x
//
//}

class Tst(a: Int, b: Int) {
  def this(c: Int) {
    this(c, 0)
  }

  def this() {
    this(0)
  }

  private val x = 0

  def other(o: Tst) = println(o.x)

}

object Tst {
  def apply(a: Int, b: Int): Tst = {
    val t = new Tst(a, b)
    println(t.x)
    t.other(new Tst())
    t
  }

}