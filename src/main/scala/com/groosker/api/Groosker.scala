package com.groosker.api

import dispatch._
import dispatch.Http._
import scala.io.Source

object Currency extends Enumeration {
  //type Currency = Value
  val CHF, USD, EUR = Value
}

case class PaymentRequestDetails(code: String, url: String)
abstract class PaymentResult
case object PaymentAccepted extends PaymentResult
case object PaymentDeclined extends PaymentResult
case object PaymentTimedOut extends PaymentResult

sealed abstract class GrooskerAbstract {
  val apiKey: String
  final val version = "1"
  def baseUrl: String
  def secure: Boolean

  import Currency._
  def createPaymentRequest(amount: BigDecimal, currency: Currency.Value, description: String, details: String): Option[PaymentRequestDetails] = {
    val http = new Http
    val req = url(baseUrl) / requestPaymentUrl.tail.mkString("/") << Map(
      "api_key" -> "123456789",
      "version" -> version,
      "currency" -> currency.toString,
      "description" -> description,
      "details" -> details,
      "amount" -> amount.toString)

    http x (req >|) {
      case (code, x, y, z) =>
        if (code == 200) {
          val txt = Source.fromInputStream(y.get.getContent).getLines.next
          import net.liftweb.json._
          implicit val formats = DefaultFormats
          val json = parse(txt)
          println(txt)
          Some(json.extract[PaymentRequestDetails])
        } else None
    }
  }

  def acceptTestPayment(code: String) = {
    val http = new Http
    val req = url(baseUrl) / acceptTestPaymentUrl.tail.mkString("/") << Map(
      "api_key" -> "123456789",
      "version" -> version,
      "code" -> code)
    http x (req >|) {
      case (code, x, y, z) =>
        if (code == 200) {
          val txt = Source.fromInputStream(y.get.getContent).getLines.next
          import net.liftweb.json._
          println(txt)
          implicit val formats = DefaultFormats
          val json = parse(txt)
          Some(json.extract[PaymentRequestDetails])
        } else {
          println(code)
          None
        }
    }

  }

  def awaitPayment(paymentId: String): PaymentResult = {
    val http = new Http
    val req = url(baseUrl) / awaitPaymentUrl.tail.mkString("/") << Map(
      "api_key" -> "123456789",
      "version" -> version,
      "code" -> paymentId)
    http x (req >|) {
      case (code, x, y, z) =>
        if (code == 200) {
          val txt = Source.fromInputStream(y.get.getContent).getLines.next
          import net.liftweb.json._
          implicit val formats = DefaultFormats
          val json = parse(txt)
          Some(json.extract[PaymentRequestDetails])
          PaymentAccepted
        } else PaymentDeclined
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
