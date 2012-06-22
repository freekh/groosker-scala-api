package com.groosker.api

import dispatch._
import dispatch.Http._
import scala.io.Source

object Currency extends Enumeration {
  //type Currency = Value
  val CHF, USD, EUR = Value
}

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
    val req = url(baseUrl) / requestPaymentUrl.tail.mkString("/") << Map(
      "api_key" -> apiKey,
      "version" -> version,
      "currency" -> currency.toString,
      "description" -> description,
      "details" -> details,
      "amount" -> amount.toString)

    http x (req >|) {
      case (200, _, y, _) =>
        val txt = Source.fromInputStream(y.get.getContent).getLines.next
        import net.liftweb.json._
        implicit val formats = DefaultFormats
        val json = parse(txt)
        println(json)
        Some(json.extract[PaymentRequestDetails])
      case (code, _, y, _) =>
        None
    }
  }

  def acceptTestPayment(code: String) = {
    val http = new Http
    val req = url(baseUrl) / acceptTestPaymentUrl.tail.mkString("/") << Map(
      "api_key" -> apiKey,
      "version" -> version,
      "code" -> code)
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
    val req = url(baseUrl) / awaitPaymentUrl.tail.mkString("/") << Map(
      "api_key" -> apiKey,
      "version" -> version,
      "code" -> paymentId)
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
