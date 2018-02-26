package services

import javax.inject.Singleton

import domain.BankDomain.{BankDB, banks}
import domain.BoletoGatewayDomain._
import domain.BoletoTransactionDomain._
import domain.CascadeLogDomain.{CascadeLogDB, CascadeLogItemDB, cascadeLogItems, cascadeLogs}
import domain.EstablishmentDomain._
import domain.NormalizedStatusDomain.{NormalizedStatusDB, normalizedStatus}
import services.RepositoryUtils.db
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.{ExecutionContext, Future}

sealed trait TransactionRepository {

  def findTransactionsByReference(referenceCode: String)(implicit executionContext: ExecutionContext): Future[Transaction]
}

@Singleton
class TransactionRepositoryImpl extends TransactionRepository {
  override def findTransactionsByReference(referenceCode: String)(implicit executionContext: ExecutionContext): Future[Transaction] = {
    val transactionData = for {
      t <- transactions if t.referenceCode === referenceCode
      e <- establishments if t.establishmentId === e.id
      bo <- boletos if t.boletoId === bo.id
      ba <- banks if t.bankId === ba.id
      n <- normalizedStatus if t.normalizedStatusId === n.id
      c <- cascadeLogs if t.cascadeLogId === c.id
      ci <- cascadeLogItems if ci.cascadeLogId === c.id
      p <- payments if p.transactionId === t.id
    } yield (t, e, bo, ba, n, c, ci, p)

    db.run(transactionData.result)
      .map(seq => teste(seq))
      .map { tuple =>
        val establishment = new Establishment(tuple._2)
        val boleto = new Boleto(tuple._3)
        val bank = new Bank(tuple._4)
        val normalizedStatus = new NormalizedStatus(tuple._5)
        val cascadeLog = new CascadeLog(tuple._6, tuple._7.map(new CascadeLogItem(_)))
        val payments = tuple._8.map(new Payment(_))

        new Transaction(tuple._1, establishment, boleto, Option(bank), Option(normalizedStatus), Option(cascadeLog), payments)
      }
  }

  private def teste(seq: Seq[(TransactionDB, EstablishmentDB, BoletoDB, BankDB, NormalizedStatusDB, CascadeLogDB, CascadeLogItemDB, PaymentDB)]) : (TransactionDB, EstablishmentDB, BoletoDB, BankDB, NormalizedStatusDB, CascadeLogDB, Seq[CascadeLogItemDB], Seq[PaymentDB]) = {
    val head = seq.head
    val seqCascadeLogItem = seq.map(t => t._7)
    val seqPayment = seq.map(t => t._8)

    (head._1, head._2, head._3, head._4, head._5, head._6, seqCascadeLogItem, seqPayment)
  }
}
