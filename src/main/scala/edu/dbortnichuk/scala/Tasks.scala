package edu.dbortnichuk.scala

object Tasks extends App {

  def fromRoman(in: List[Char]): Int = in match {
    case 'I' :: 'V' :: xs => 4 + fromRoman(xs)
    case 'I' :: 'X' :: xs => 9 + fromRoman(xs)
    case 'I' :: xs => 1 + fromRoman(xs)
    case 'V' :: xs => 5 + fromRoman(xs)
    case 'X' :: 'L' :: xs => 40 + fromRoman(xs)
    case 'X' :: 'C' :: xs => 90 + fromRoman(xs)
    case 'X' :: xs => 10 + fromRoman(xs)
    case 'L' :: xs => 50 + fromRoman(xs)
    case 'C' :: 'D' :: xs => 400 + fromRoman(xs)
    case 'C' :: 'M' :: xs => 900 + fromRoman(xs)
    case 'C' :: xs => 100 + fromRoman(xs)
    case 'D' :: xs => 500 + fromRoman(xs)
    case 'M' :: xs => 1000 + fromRoman(xs)
    case _ => 0
  }


  def toRoman(v: Int): String = {
    val romanNumerals = List(1000 -> "M", 900 -> "CM", 500 -> "D", 400 -> "CD", 100 -> "C", 90 -> "XC",
      50 -> "L", 40 -> "XL", 10 -> "X", 9 -> "IX", 5 -> "V", 4 -> "IV", 1 -> "I")

    var n = v
    romanNumerals.foldLeft("") {
      (roman, mapping) => {
        val c = n / mapping._1
        n = n - mapping._1 * c
        roman + (mapping._2 * c)
      }
    }
  }

  println(toRoman(1618))

  println(fromRoman("MDCXVIII".toList))

  println(toRoman(fromRoman("MDCXVIII".toList)))

  def fib3( n : Int) : Int = {
    def fib_tail( n: Int, a:Int, b:Int): Int = n match {
      case 0 => a
      case _ => fib_tail( n-1, b, a+b )
    }
    fib_tail(n, 0, 1)
  }

  println(fib3(10))

  def factorial(number: Int): Int = {
    def factorialLoop(accumulator: Int, number: Int): Int = {
      if (number == 1) return accumulator
      factorialLoop(number * accumulator, number - 1)
    }
    factorialLoop(1, number)
  }
  println(factorial(5))

}
