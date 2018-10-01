package service

import java.time.LocalDate

import controllers.PaymentCreateRequest
import domain.{Payment, Transaction}
import javax.inject.{Inject, Singleton}
import repository.{PaymentRepository, TransactionRepository}

import scala.concurrent.{ExecutionContext, Future}

sealed trait PaymentService {
  def createPayment(transactionId: Long, request: PaymentCreateRequest)(implicit ec: ExecutionContext): Future[Transaction]
}

@Singleton
class PaymentServiceImpl @Inject()(transactionRepository: TransactionRepository, paymentRepository: PaymentRepository, transactionService: TransactionService) extends PaymentService  {
  override def createPayment(transactionId: Long, request: PaymentCreateRequest)(implicit ec: ExecutionContext): Future[Transaction] = {
    transactionRepository.findById(transactionId)
      .flatMap {
        case None => Future.failed(NotFoundTransactionException())
        case optTransaction =>
          optTransaction.filter(!isAlreadyPaid(_))
            .map { t =>
              val user = request.user
              val time = System.currentTimeMillis().toString
              val payment = Payment(id = 1, amount = t.boleto.amount, creator = user, nsa = "0", nsr = "0", paymentDate = LocalDate.now(), creditDate = LocalDate.now(), notificationId = Option(s"$user-criado-manualmente-$time"), creation = LocalDate.now())
              paymentRepository.save(payment, transactionId)
                .flatMap(_ => transactionRepository.findById(transactionId) // Busca a transação dnv para pegar a que tem o pagamento
                  .flatMap(t => transactionService.changeStatus(t.get)))
          }.getOrElse(Future.failed(AlreadyPaidTransactionException()))
      }
  }

  private def isAlreadyPaid(transaction: Transaction): Boolean = {
    "paid" == transaction.status || "partially-paid" == transaction.status
  }
}

case class NotFoundTransactionException() extends RuntimeException
case class AlreadyPaidTransactionException() extends RuntimeException

