package com.groosker.api

import dispatch._
import scala.io.Source
import java.util.{ Date, Calendar }
import scala.concurrent.Future
import scala.concurrent.duration.Duration
import scala.concurrent.Await
import dispatch.as.Response
import com.ning.http.client.Response
import scala.util.control.Exception
import scala.concurrent.ExecutionContext.Implicits.global

object Currency extends Enumeration {
  //type Currency = Value
  val BTC, USD, EUR, GBP, CHF, NOK = Value
  def find(currency: String) = this.values.find(currency.toUpperCase == _.toString)
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

  val couldNotParseMessage = "Could not parse data from Groosker"
  import Currency._
  import net.liftweb.json._
  implicit val formats = DefaultFormats
  val mappingCatcher = Exception.catching(classOf[MappingException])
  def createPaymentRequest(amount: BigDecimal, currency: Currency.Value, description: String): Future[Either[String, PaymentRequestDetails]] = {
    val http = new Http
    val params = RequestPayment.paramDef.params zip Seq(apiKey, version, amount.toString, currency.toString, description)
    val req = url(baseUrl) / RequestPayment.apiCall << params
    Http(req > (_ match {
      case r if r.getStatusCode == 200 =>
        val json = r.getResponseBody
        val res = mappingCatcher either parse(json).extract[PaymentRequestDetails]
        res.left map (x => couldNotParseMessage)
      case r =>
        Left(r.getResponseBody)
    }))
  }

  def acceptTestPayment(code: String): Future[Either[String, PaymentRequestDetails]] = {
    val params = AcceptTestPayment.paramDef.params zip (List(apiKey, version, code))
    val req = url(baseUrl) / AcceptTestPayment.apiCall << params
    Http(req > (_ match {
      case r if r.getStatusCode == 200 =>
        val json = r.getResponseBody
        val res = mappingCatcher either parse(json).extract[PaymentRequestDetails]
        res.left map (x => couldNotParseMessage)
      case r =>
        Left(r.getResponseBody)
    }))
  }

  def awaitPayment(paymentId: String): Future[PaymentResult] = {
    val http = new Http
    val params = AwaitPayment.paramDef.params zip (List(apiKey, version, paymentId))
    val req = url(baseUrl) / AwaitPayment.apiCall << params
    Http(req > (_ match {
      case r if r.getStatusCode == 200 =>
        val json = r.getResponseBody
        val res = mappingCatcher either parse(json).extract[AwaitPaymentResult].toPaymentResult
        res.fold(
          failure => PaymentResult.Failure(couldNotParseMessage),
          ok => ok)
      case r =>
        PaymentResult.Failure(r.getResponseBody)
    }))
  }
}

abstract class GrooskerTest(url: String) extends GrooskerAbstract {
  final val baseUrl = url + (if (url.endsWith("/")) "" else "/") + "api/"
}

abstract class Groosker extends GrooskerAbstract {
  final val baseUrl = "https://api.groosker.com/"
}
