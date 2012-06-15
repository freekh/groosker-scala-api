package com.groosker

package object api {

  abstract class ApiUrl {
    class Params
    def url: String
    def params: Params
  }

  object RequestPayment extends ApiUrl {
    val url = "request_payment"
    object params extends Params {
      val (apiKey, amount, currency, receiver, details, version) = ("api_key", "amount", "currency", "receiver", "details", "version")
    }
  }

  private val upayUrls = List("requestPayment", "generateQRCode", "awaitPayment", "acceptTestPayment", "declineTestPayment") map { url => List("api", url) }
  val List(requestPaymentUrl, generateQRCodeUrl, awaitPaymentUrl, acceptTestPaymentUrl, declineTestPaymentUrl) = upayUrls

  RequestPayment.url
  RequestPayment.params.amount
}