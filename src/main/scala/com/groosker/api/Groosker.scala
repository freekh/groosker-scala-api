package com.groosker.api

object Currency extends Enumeration {
  //type Currency = Value
  val CHF, USD, EUR = Value
}

case class PaymentRequestDetails(id: String, url: String)

sealed abstract class GrooskerAbstract {
  val apiKey: String
  val merchantId: String
  final val Version = "1"
  val host: String
  final val generateRefUrl = host + "getpaid/ref/create"
  final val imageUrl = host + "getpaid/ref/qrcode"

  import Currency._
  def createPaymentRequest(amount: BigDecimal, currency: Currency.Value, description: String): PaymentRequestDetails = {
    println("Requested payment: %.2f %s - %s" format (amount, currency, description))
    PaymentRequestDetails("123123123", "http://groosker.com/img/grooskerqr.png")
  }

  def awaitPayment(paymentId: String): Boolean = false

  import dispatch._

  def generateRef(amount: BigDecimal, currency: String, receiver: String, details: String): Request = url(generateRefUrl) << Map(
    "amount" -> amount.toString,
    "currency" -> currency,
    "receiver" -> receiver,
    "details" -> details,
    "version" -> Version)

  def image(ref: String): Request = url(imageUrl) << Map("code" -> ref, "version" -> Version)

}

abstract class GrooskerTest extends GrooskerAbstract {
  final val host = "http://localhost:8080/api/"
}

abstract class Groosker extends GrooskerAbstract {
  final val host = "https://spendchart.no/api/"
}
