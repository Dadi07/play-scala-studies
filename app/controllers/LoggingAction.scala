package controllers

import javax.inject.Inject

import play.api.Logger
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

class LoggingAction @Inject() (parser : BodyParsers.Default)(implicit executionContext: ExecutionContext) extends ActionBuilderImpl(parser){
  override def invokeBlock[A](request: Request[A], block: Request[A] => Future[Result]) = {
    Logger.info("Recebendo request")
    block(request)
  }
}
