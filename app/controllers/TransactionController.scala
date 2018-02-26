package controllers

import javax.inject.{Inject, Singleton}

import controllers.ResponseWriters.TransactionWrites
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}
import services.TransactionRepository

import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class TransactionController @Inject()(cc: ControllerComponents, transactionRepository: TransactionRepository) extends AbstractController(cc) {

  def searchTransaction(referenceCode: Option[String], bankNumber: Option[String], establishment: Option[String], bank: Option[String]) = Action.async {
    transactionRepository.findTransactionsByReference(referenceCode.get)
      .map(t => Ok(Json.toJson(t)))
  }

}
