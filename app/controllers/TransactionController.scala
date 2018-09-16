package controllers

import controllers.ResponseWriters.{transactionSearchWrites, transactionWrites}
import javax.inject.{Inject, Singleton}
import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}
import repository.{TransactionFilters, TransactionRepository}

import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class TransactionController @Inject()(cc: ControllerComponents, transactionRepository: TransactionRepository) extends AbstractController(cc) {

  def searchTransaction(id: Long) = Action.async {
    Logger.info(s"action=endpoint-request uri=/transacions/$id method=GET")
    transactionRepository.findById(id)
      .map { transaction =>
        if (transaction.isEmpty) {
          Logger.info(s"action=endpoint-response uri=/transacions/$id method=GET status=not-found")
          NotFound
        } else {
          Logger.info(s"action=endpoint-response uri=/transacions/$id method=GET status=ok")
          Ok(Json.toJson(transaction))
        }
      }
  }

  def searchTransactions(referenceCode: Option[String], bankNumber: Option[String], establishment: Option[String], bankAgreement: Option[String], bank: Option[String], status: Option[String], amount: Option[Int]) = Action.async {
    val transactionFilters = TransactionFilters(referenceCode, bankNumber, establishment, bankAgreement, bank, Option.empty, status, amount) // TODO entender o que eu tava pensando qnd coloquei o normalizedStatus no filtro
    Logger.info(s"action=endpoint-request uri=/transacions method=GET $transactionFilters")
    transactionRepository.findTransactionsByFilter(transactionFilters)
      .map { seq =>
        Logger.info(s"action=endpoint-response uri=/transacions method=GET $transactionFilters ${seq.size} transactions found")
        if (seq.isEmpty) NotFound else Ok(Json.toJson(seq))
      }
  }

}
