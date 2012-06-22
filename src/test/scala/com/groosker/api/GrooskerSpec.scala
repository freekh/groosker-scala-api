package com.groosker.api

import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import org.junit.runner.RunWith
import akka.dispatch.Future

@RunWith(classOf[JUnitRunner])
class GrooskerSpec extends Specification {
  "Calling SpendChart API" should {
    "return a payment id and a qr code" in {
      val g = new GrooskerTest {
        val apiKey = "123456789"
      }

      val Some(PaymentRequestDetails(code, url)) = g.createPaymentRequest(BigDecimal(12.0), Currency.CHF, "T-shirt", "Details")
      url must contain("/api/image/")
      url must contain(code)
      Future { Thread.sleep(5000); g.acceptTestPayment(code) }
      g.awaitPayment(code) must be(PaymentResult.Accepted)
    }
  }

}