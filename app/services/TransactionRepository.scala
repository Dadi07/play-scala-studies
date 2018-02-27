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
//    val transactionData = for {
//      t <- transactions if t.referenceCode === referenceCode
//      e <- establishments if t.establishmentId === e.id
//      bo <- boletos if t.boletoId === bo.id
//      ba <- banks if t.bankId === ba.id
//      n <- normalizedStatus if t.normalizedStatusId === n.id
//      c <- cascadeLogs if t.cascadeLogId === c.id
//      ci <- cascadeLogItems if ci.cascadeLogId === c.id
//      p <- payments if p.transactionId === t.id
//    } yield (t, e, bo, ba, n, c, ci, p)

//    val test = for {
//      t <- transactions if t.referenceCode === referenceCode
//      e <- establishments if t.establishmentId === e.id
//      bo <- boletos if t.boletoId === bo.id
//      (_, ba) <- transactions joinLeft banks on (_.bankId === _.id)
//      (_, n) <- transactions joinLeft normalizedStatus on (_.normalizedStatusId === _.id)
//      (_, c) <- transactions joinLeft cascadeLogs on (_.cascadeLogId === _.id)
//      (_, ci) <- cascadeLogs joinLeft cascadeLogItems on (_.id === _.cascadeLogId)
//      (_, p) <- transactions joinLeft payments on (_.id === _.transactionId)
//    } yield (t, e, bo, ba, n, c, ci, p)

    val test2 = for {
      (((((((t, e), bo), ba), n), c), ci),p) <- transactions join establishments on (_.establishmentId === _.id) join boletos on (_._1.boletoId === _.id) joinLeft banks on (_._1._1.bankId === _.id) joinLeft  normalizedStatus on (_._1._1._1.normalizedStatusId === _.id) joinLeft cascadeLogs on (_._1._1._1._1.cascadeLogId === _.id) joinLeft cascadeLogItems on (_._2.map(_.id) === _.cascadeLogId) joinLeft payments on (_._1._1._1._1._1._1.id === _.transactionId)
      if t.referenceCode === referenceCode
    } yield (t, e, bo, ba, n, c, ci, p)

//    test2.filter { tuple =>
//      Option(referenceCode).map(r => tuple._1.referenceCode === r).getOrElse(slick.lifted.LiteralColumn(true))
//    }

    db.run(test2.result)
      .map(seq => mapTableRows(seq))
      .map { tuple =>
        val establishment = new Establishment(tuple._2)
        val boleto = new Boleto(tuple._3)
        val optionBank = tuple._4.map(new Bank(_))
        val optionNormalizedStatus = tuple._5.map(new NormalizedStatus(_))
        val cascadeLog = tuple._6.map(new CascadeLog(_, tuple._7.map(new CascadeLogItem(_))))
        val paymentsList = tuple._8.map(new Payment(_))

        new Transaction(tuple._1, establishment, boleto, optionBank, optionNormalizedStatus, cascadeLog, paymentsList)
      }
  }

  private def teste(seq: Seq[(TransactionDB, EstablishmentDB, BoletoDB, BankDB, NormalizedStatusDB, CascadeLogDB, CascadeLogItemDB, PaymentDB)]): (TransactionDB, EstablishmentDB, BoletoDB, BankDB, NormalizedStatusDB, CascadeLogDB, Seq[CascadeLogItemDB], Seq[PaymentDB]) = {
    val head = seq.head
    val seqCascadeLogItem = seq.map(t => t._7).distinct
    val seqPayment = seq.map(t => t._8).distinct

    (head._1, head._2, head._3, head._4, head._5, head._6, seqCascadeLogItem, seqPayment)
  }

  private def mapTableRows(seq: Seq[(TransactionDB, EstablishmentDB, BoletoDB, Option[BankDB], Option[NormalizedStatusDB], Option[CascadeLogDB], Option[CascadeLogItemDB], Option[PaymentDB])]): (TransactionDB, EstablishmentDB, BoletoDB, Option[BankDB], Option[NormalizedStatusDB], Option[CascadeLogDB], Seq[CascadeLogItemDB], Seq[PaymentDB]) = {
    val head = seq.head
    val seqCascadeLogItem = seq.map(_._7).filter(_.isDefined).map(_.get).distinct
    val seqPayment = seq.map(_._8).filter(_.isDefined).map(_.get).distinct

    (head._1, head._2, head._3, head._4, head._5, head._6, seqCascadeLogItem, seqPayment)
  }
}
