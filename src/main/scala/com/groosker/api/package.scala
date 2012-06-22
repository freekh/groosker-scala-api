package com.groosker

package object api {

  abstract class ApiUrl {
    abstract class Params {
      val params: List[String]
      val commonParams = List("api_key", "version")
      val List(apiKey, version) = commonParams
    }
    def url: String
    def params: Params
  }

  object RequestPayment extends ApiUrl {
    val url = "request_payment"
    object params extends Params {
      val params = List("amount", "currency", "receiver", "details")
      val List(amount, currency, receiver, details) = params
    }
  }

  object AwaitPayment extends ApiUrl {
    val url = "await_payment"
    object params extends Params {
      val params = List("code")
      val (code) = params
    }
  }

  object AcceptTestPayment extends ApiUrl {
    val url = "accept_test_payment"
    object params extends Params {
      val params = List("code")
      val (code) = params
    }
  }

  object DeclineTestPayment extends ApiUrl {
    val url = "decline_test_payment"
    object params extends Params {
      val params = List("code")
      val (code) = params
    }
  }

  private val upayUrls = List("requestPayment", "generateQRCode", "awaitPayment", "acceptTestPayment", "declineTestPayment") map { url => List("api", url) }
  val List(requestPaymentUrl, generateQRCodeUrl, awaitPaymentUrl, acceptTestPaymentUrl, declineTestPaymentUrl) = upayUrls

}