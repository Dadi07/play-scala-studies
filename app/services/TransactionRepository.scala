package services

import javax.inject.Singleton

import domain.BankDomain
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

  def findTransactionsByReference(referenceCode: Option[String], bank: Option[String])(implicit executionContext: ExecutionContext): Future[Seq[Transaction]]
}

@Singleton
class TransactionRepositoryImpl extends TransactionRepository {
  override def findTransactionsByReference(referenceCode: Option[String], bank: Option[String])(implicit executionContext: ExecutionContext): Future[Seq[Transaction]] = {
    val filterTransaction = referenceCode.map(r => transactions.filter(_.referenceCode === r)).getOrElse(transactions)
    val filterBank = bank.map(s => banks.filter(_.code === s)).getOrElse(banks)

    val transactionQuery = for {
      (((((((t, e), bo), ba), n), c), ci),p) <- filterTransaction join establishments on (_.establishmentId === _.id) join boletos on (_._1.boletoId === _.id) joinLeft filterBank on (_._1._1.bankId === _.id) joinLeft  normalizedStatus on (_._1._1._1.normalizedStatusId === _.id) joinLeft cascadeLogs on (_._1._1._1._1.cascadeLogId === _.id) joinLeft cascadeLogItems on (_._2.map(_.id) === _.cascadeLogId) joinLeft payments on (_._1._1._1._1._1._1.id === _.transactionId)
    } yield (t, e, bo, ba, n, c, ci, p)

    db.run(transactionQuery.take(100).result)
      .map(seq => mapTableRows(seq))
      .map { _.map { tuple =>
          val establishment = new Establishment(tuple._2)
          val boleto = new Boleto(tuple._3)
          val optionBank = tuple._4.map(new Bank(_))
          val optionNormalizedStatus = tuple._5.map(new NormalizedStatus(_))
          val cascadeLog = tuple._6.map(new CascadeLog(_, tuple._7.map(new CascadeLogItem(_))))
          val paymentsList = tuple._8.map(new Payment(_))

          new Transaction(tuple._1, establishment, boleto, optionBank, optionNormalizedStatus, cascadeLog, paymentsList)
        }
      }
  }

  private def mapTableRows(seq: Seq[(TransactionDB, EstablishmentDB, BoletoDB, Option[BankDB], Option[NormalizedStatusDB], Option[CascadeLogDB], Option[CascadeLogItemDB], Option[PaymentDB])]): Seq[(TransactionDB, EstablishmentDB, BoletoDB, Option[BankDB], Option[NormalizedStatusDB], Option[CascadeLogDB], Seq[CascadeLogItemDB], Seq[PaymentDB])] = {
    if (seq.isEmpty) {
      Seq.empty
    } else {
      val transactionsTuplesMap = seq.groupBy(_._1.id)

      transactionsTuplesMap.values.map { v =>
        val head = v.head
        val seqCascadeLogItem = seq.map(_._7).filter(_.isDefined).map(_.get).distinct
        val seqPayment = seq.map(_._8).filter(_.isDefined).map(_.get).distinct

        (head._1, head._2, head._3, head._4, head._5, head._6, seqCascadeLogItem, seqPayment)
      }.toIndexedSeq
    }
  }
}
