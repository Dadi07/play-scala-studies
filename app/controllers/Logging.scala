package controllers

import play.api.Logger
import play.api.mvc.{Action, BodyParser, Request, Result}

import scala.concurrent.{ExecutionContext, Future}

case class Logging [A] (action: Action[A]) extends Action[A] {

  def apply(request : Request[A]) : Future[Result] = {
    val requestBody = request.body
    Logger.info(s"Recebendo request: $request with $requestBody")
    action(request)
  }

  override def parser: BodyParser[A] = action.parser

  override def executionContext: ExecutionContext = action.executionContext
}
