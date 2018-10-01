package service

import java.time.LocalDate

import domain.{NormalizedStatus, Transaction}
import javax.inject.{Inject, Singleton}
import repository.{NormalizedStatusRepository, TransactionRepository}

import scala.concurrent.{ExecutionContext, Future}

sealed trait TransactionService {
  def changeStatus(transaction: Transaction)(implicit ec: ExecutionContext): Future[Transaction]
}

@Singleton
class TransactionServiceImpl @Inject()(transactionRepository: TransactionRepository, normalizedStatusRepository: NormalizedStatusRepository) extends TransactionService {
  override def changeStatus(transaction: Transaction)(implicit ec: ExecutionContext): Future[Transaction] = {
    val paymentsSum = transaction.payments.map(_.amount).sum

    //TODO verificar se nao tem nenhum pagamento
    val isFullPayment = paymentsSum >= transaction.boleto.amount

    val status = if(isFullPayment) "paid" else "partially-paid"
    val normalizedStatusCode = if(isFullPayment) NormalizedStatus.PaidCode else NormalizedStatus.PartiallyPaidCode

    normalizedStatusRepository.findByCode(Option(normalizedStatusCode))
      .map(_.head)
      .flatMap { ns =>
        transaction.paidAmount = Option(paymentsSum)
        transaction.paymentDate = Option(LocalDate.now())
        transaction.status = status
        transaction.normalizedStatus = Option(ns)

        transactionRepository.updateStatus(transaction)
          .map(_ => transaction)
      }
  }
}
