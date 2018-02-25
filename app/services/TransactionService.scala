package services

import javax.inject.{Inject, Singleton}

import domain.BoletoGatewayDomain.{Establishment, Transaction}

import scala.concurrent.Future

sealed trait TransactionService {

  def searchTransaction(establishment: String) : Future[Seq[Transaction]]
}

@Singleton
class TransactionServiceImpl @Inject()(transactionRepository: TransactionRepository) extends TransactionService {

  override def searchTransaction(referenceCode: String): Future[Seq[Transaction]] = {
    establishmentRepository.findByCode(establishmentCode).map { e =>
      val establishment = Establishment(e.id, e.name, e.code)

      transactionRepository.findTransactionsByReference(establishment.id)
    }
  }

  private def
}
