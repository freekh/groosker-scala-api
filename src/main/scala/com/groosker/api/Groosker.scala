package com.groosker.api

import dispatch._
import dispatch.Http._
import scala.io.Source

object Currency extends Enumeration {
  //type Currency = Value
  val BTC, USD, EUR, GBP, CHF, NOK = Value
}

import java.util.{ Date, Calendar }

case class PaymentRequestDetails(code: String, url: String)
case class AwaitPaymentResult(result: String) {
  import PaymentResult._
  def toPaymentResult = result match {
    case x if x == Accepted.toString => Accepted
    case x if x == Declined.toString => Declined
  }
}

abstract class PaymentResult

object PaymentResult {
  case object Accepted extends PaymentResult
  case object Declined extends PaymentResult
  case object TimedOut extends PaymentResult
  case class Failure(error: String) extends PaymentResult
}

sealed abstract class GrooskerAbstract {
  val apiKey: String
  final val version = "1"
  def baseUrl: String
  def secure: Boolean

  import Currency._
  def createPaymentRequest(amount: BigDecimal, currency: Currency.Value, description: String, details: String): Option[PaymentRequestDetails] = {
    val http = new Http
    val params = RequestPayment.paramDef.params zip Seq(apiKey, version, amount.toString, currency.toString, details)
    val req = url(baseUrl) / RequestPayment.apiCall << params

    http x (req >|) {
      case (200, _, y, _) =>
        val txt = Source.fromInputStream(y.get.getContent).getLines.mkString("\n")
        import net.liftweb.json._
        implicit val formats = DefaultFormats
        val json = parse(txt)
        Some(json.extract[PaymentRequestDetails])
      case (code, _, y, _) =>
        None
    }
  }

  def acceptTestPayment(code: String) = {
    val http = new Http
    val params = AcceptTestPayment.paramDef.params zip (List(apiKey, version, code))
    val req = url(baseUrl) / AcceptTestPayment.apiCall << params
    http x (req >|) {
      case (code, x, y, z) =>
        if (code == 200) {
          val txt = Source.fromInputStream(y.get.getContent).getLines.mkString("\n")
          import net.liftweb.json._
          implicit val formats = DefaultFormats
          val json = parse(txt)
          Some(json.extract[PaymentRequestDetails]) // TODO: Change me
        } else {
          println(code)
          None
        }
    }

  }

  def awaitPayment(paymentId: String): PaymentResult = {
    val http = new Http
    val params = AwaitPayment.paramDef.params zip (List(apiKey, version, paymentId))
    val req = url(baseUrl) / AwaitPayment.apiCall << params
    http x (req >|) {
      case (200, x, y, z) =>
        val txt = Source.fromInputStream(y.get.getContent).getLines.mkString("\n")
        import net.liftweb.json._
        implicit val formats = DefaultFormats
        val json = parse(txt)
        println("From await payment: " + json)
        json.extract[AwaitPaymentResult].toPaymentResult
      case (code, _, out, _) =>
        val txt = Source.fromInputStream(out.get.getContent).getLines.mkString("\n")
        println("got %d: %s" format (code, txt))
        PaymentResult.Failure(txt)
    }

  }

}

abstract class GrooskerTest extends GrooskerAbstract {
  final val baseUrl = "http://localhost:8080/api/"
  val secure = false
}

abstract class Groosker extends GrooskerAbstract {
  final val baseUrl = "https://api.groosker.com/"
  val secure = true
}
