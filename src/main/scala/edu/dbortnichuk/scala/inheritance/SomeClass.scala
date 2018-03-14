package edu.dbortnichuk.scala.inheritance

class SomeClass(private[scala] var x: String, val y: String) {

  private[inheritance] val z = "z"


}

object SomeOtherClass extends App {

  val sc = new SomeClass("x", "y")
  sc.x = "x1"
  sc.y
  sc.z

  println(sc.x)


}
