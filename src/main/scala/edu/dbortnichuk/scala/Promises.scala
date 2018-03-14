package edu.dbortnichuk.scala

import concurrent.Promise
import concurrent.Future
import concurrent.ExecutionContext.Implicits.global

case class TaxCut(reduction: Int)
case class LameExcuse(msg: String) extends Exception(msg)

object Promises extends App {

  def redeemCampaignPledge(): Future[TaxCut] = {
    val p = Promise[TaxCut]()
    Future {
      println("Starting the new legislative period.")
      Thread.sleep(2000)
      p.success(TaxCut(20))
      println("We reduced the taxes! You must reelect us!!!!1111")
    }
    p.future
  }

  def failCampaignPledge(): Future[TaxCut] = {
    val p = Promise[TaxCut]()
    Future {
      println("Starting the new legislative period.")
      Thread.sleep(2000)
      p.failure(LameExcuse("global economy crisis"))
      println("We didn't fulfill our promises, but surely they'll understand.")
    }
    p.future
  }

  import scala.util.{Success, Failure}

  //val taxCutF: Future[TaxCut] = redeemCampaignPledge()
  val taxCutF: Future[TaxCut] = failCampaignPledge()
  println("Now that they're elected, let's see if they remember their promises...")
  taxCutF.onComplete {
    case Success(TaxCut(reduction)) =>
      println(s"A miracle! They really cut our taxes by $reduction percentage points!")
    case Failure(ex) =>
      println(s"They broke their promises! Again! Because of a ${ex.getMessage}")
  }

  Thread.sleep(5000)



}
