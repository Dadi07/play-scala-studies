package controllers

import javax.inject.{Inject, Singleton}
import play.api.libs.json.{JsValue, Json, Reads}
import play.api.mvc.{AbstractController, ControllerComponents}
import play.api.{Logger, mvc}
import service.{AlreadyPaidTransactionException, NotFoundTransactionException, PaymentService}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

@Singleton
class PaymentController @Inject()(cc: ControllerComponents, paymentService: PaymentService) extends AbstractController(cc) {

  implicit val createPaymentRead: Reads[PaymentCreateRequest] = Json.reads[PaymentCreateRequest]

  def createPayment(id: Long) = Action.async(parse.json) { request =>
    Logger.info(s"action=endpoint-request uri=${request.uri} method=${request.method} request=${request.body}")
    val paymentCreateRequest = request.body.as[PaymentCreateRequest]

    val result = paymentService.createPayment(id, paymentCreateRequest)
      .map(t => Created)
      .recover {
        case e: NotFoundTransactionException => NotFound
        case e: AlreadyPaidTransactionException => BadRequest
      }

    //TODO chamar a api de notificação em caso de sucesso

    result
      .onComplete {
        case Success(value) => Logger.info(s"action=endpoint-response httpStatus=${value.header.status} uri=${request.uri} method=${request.method}")
        case Failure(exception) => Logger.info(s"action=endpoint-response httpStatus=500 uri=${request.uri} method=${request.method}")
      }

    result
  }

//  def searchTransactions(referenceCode: Option[String], bankNumber: Option[String], establishment: Option[String], bankAgreement: Option[String], bank: Option[String], status: Option[String], amount: Option[Int]) = Action.async {
//    val transactionFilters = TransactionFilters(referenceCode, bankNumber, establishment, bankAgreement, bank, Option.empty, status, amount) // TODO entender o que eu tava pensando qnd coloquei o normalizedStatus no filtro
//    Logger.info(s"action=endpoint-request uri=/transacions method=GET $transactionFilters")
//    transactionRepository.findTransactionsByFilter(transactionFilters)
//      .map { seq =>
//        Logger.info(s"action=endpoint-response uri=/transacions method=GET $transactionFilters ${seq.size} transactions found")
//        if (seq.isEmpty) NotFound else Ok(Json.toJson(seq))
//      }
//  }

}

case class PaymentCreateRequest(user: String, reason: String)
