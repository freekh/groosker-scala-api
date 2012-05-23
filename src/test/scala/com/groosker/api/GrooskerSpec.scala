package com.groosker.api

import org.specs2.mutable.Specification

class GrooskerSpec extends Specification {
  "Calling SpendChart API" should {
    "return a payment id and a qr code" in {
      val g = new GrooskerTest {
        val merchantId = ""
        val apiKey = ""
      }

      val PaymentRequestDetails(id, url) = g.createPaymentRequest(BigDecimal(12.0), Currency.CHF, "T-shirt")
      url must startWith("http://localhost:8080")
    }
  }

}