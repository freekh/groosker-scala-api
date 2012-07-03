package com.groosker

package object api {

  abstract class Params {
    val params = commonParams ++ callParams
    val callParams: List[String]
    val result: List[String]
    val commonParams = List("api_key", "version")
    val List(apiKey, version) = commonParams
  }
  abstract class ApiUrl {
    def url: String
    def params: Params
    //    def unapply(x: Any) = x match {
    //      case r @ Req(List("api", `url`), _, PostRequest) => Some(r, r.paramNames.toSet)
    //      case _ => None
    //    }

  }

  object RequestPayment extends ApiUrl {
    val url = "request_payment"
    object params extends Params {
      val callParams = List("amount", "currency", "receiver", "details")
      val List(amount, currency, receiver, details) = params
      val result = List("code", "url")
      val List(code, url) = result
    }
  }

  object AwaitPayment extends ApiUrl {
    val url = "await_payment"
    object params extends Params {
      val callParams = List("code")
      val (code) = params
      val result = List("result")
    }
  }

  object AcceptTestPayment extends ApiUrl {
    val url = "accept_test_payment"
    object params extends Params {
      val callParams = List("code")
      val (code) = params
      val result = List("result")
    }
  }

  object DeclineTestPayment extends ApiUrl {
    val url = "decline_test_payment"
    object params extends Params {
      val callParams = List("code")
      val (code) = params
      val result = List("result")
    }
  }

  private val upayUrls = List("requestPayment", "generateQRCode", "awaitPayment", "acceptTestPayment", "declineTestPayment") map { url => List("api", url) }
  val List(requestPaymentUrl, generateQRCodeUrl, awaitPaymentUrl, acceptTestPaymentUrl, declineTestPaymentUrl) = upayUrls
}