package com.groosker.api

import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import org.junit.runner.RunWith
import scala.concurrent.Future
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global

@RunWith(classOf[JUnitRunner])
class GrooskerSpec extends Specification {
  "Calling SpendChart API" should {
    "return a payment id and a qr code" in {
      val g = new Groosker {
        val apiKey = "123456789"
      }
      Await.result(g.createPaymentRequest(BigDecimal(12.0), Currency.BTC, "Tshirt"), Duration.Inf) match {
        case Right(PaymentRequestDetails(url, code)) =>
          url must contain("/api/image/")
          url must contain(code)
          Future { Thread.sleep(500); g.acceptTestPayment(code) }
          g.awaitPayment(code) must be(PaymentResult.Accepted)
        case Left(t) =>
          println(t)
          false must be_===(true)
      }
    }
  }

}