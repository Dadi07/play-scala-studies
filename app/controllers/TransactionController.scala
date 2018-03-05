package controllers

import javax.inject.{Inject, Singleton}

import controllers.ResponseWriters.transactionWrites
import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}
import services.{TransactionFilters, TransactionRepository}

import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class TransactionController @Inject()(cc: ControllerComponents, transactionRepository: TransactionRepository) extends AbstractController(cc) {

  def searchTransaction(referenceCode: Option[String], bankNumber: Option[String], establishment: Option[String], bank: Option[String], status: Option[String], amount: Option[Int]) = Action.async {
    val transactionFilters = TransactionFilters(referenceCode, bankNumber, establishment, bank, status, amount)
    Logger.info(s"action=endpoint-request uri=/transacions method=GET $transactionFilters")
    transactionRepository.findTransactionsByReference(transactionFilters)
      .map { seq =>
        val size = seq.size
        Logger.info(s"action=endpoint-response uri=/transacions method=GET $transactionFilters $size transactions found")
        if (seq.isEmpty) NotFound else Ok(Json.toJson(seq))
      }
  }

}
