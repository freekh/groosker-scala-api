package com.groosker

package object api {
  type ParamsMap = Map[String, String]

  case class ApiPostRequest(command: String, params: ParamsMap)

  abstract class Params {
    val mainParams = List("api_key", "version")
    val commonParams = mainParams
    val callParams: List[String]
    val params = commonParams ++ callParams
    val result: List[String]
    val List(apiKey, version) = mainParams
  }

  abstract class ApiCall {
    def apiCall: String
    def paramDef: Params
    def getResult(x: ParamsMap): Either[String, ApiResult]
    def checkRequired(inParams: ParamsMap): Option[String] =
      if (paramDef.params.forall(inParams.contains(_)))
        None else Some("Not the right params. Missing: " + paramDef.params.diff(inParams.keys.toSeq).mkString("", ", ", "."))
    def unapply(x: Any) = x match {
      case r @ ApiPostRequest(cmd, params) if cmd == apiCall =>
        getResult(params).fold({ error => println(error); None }, res => Some(res))
      case _ => None
    }
  }
  class ApiResult

  object RequestPayment extends ApiCall {
    val apiCall = "request_payment"
    def getResult(inParams: ParamsMap) = checkRequired(inParams).toLeft(new ApiResult)
    val paramDef = new {
      val callParams = List("amount", "currency", "details")
    } with Params {
      val List(amount, currency, details) = callParams
      val result = List("code", "url")
      val List(code, url) = result
    }
  }

  object Quote extends ApiCall {
    val apiCall = "quote"
    def getResult(inParams: ParamsMap) = checkRequired(inParams).toLeft(new ApiResult)
    val paramDef = new {
      override val commonParams = List.empty
      val callParams = List("currency")
    } with Params {
      val List(currency) = callParams
      val result = List("quote")
      val List(quote) = result
    }
  }

  
  object AwaitPayment extends ApiCall {
    val apiCall = "await_payment"
    def getResult(inParams: ParamsMap) = checkRequired(inParams).toLeft(new ApiResult)
    val paramDef = new {
      val callParams = List("code")
    } with Params {
      val List(code) = callParams
      val result = List("result")
    }
  }

  object AcceptTestPayment extends ApiCall {
    val apiCall = "accept_test_payment"
    def getResult(inParams: ParamsMap) = checkRequired(inParams).toLeft(new ApiResult)
    val paramDef = new {
      val callParams = List("code")
    } with Params {
      val List(code) = callParams
      val result = List("result")
    }
  }

  object DeclineTestPayment extends ApiCall {
    val apiCall = "decline_test_payment"
    def getResult(inParams: ParamsMap) = checkRequired(inParams).toLeft(new ApiResult)
    val paramDef = new {
      val callParams = List("code")
    } with Params {
      val List(code) = callParams
      val result = List("result")
    }
  }
}
