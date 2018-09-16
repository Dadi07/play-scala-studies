package repository

import domain._
import javax.inject.Singleton
import repository.RepositoryUtils.db
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.{ExecutionContext, Future}

sealed trait TransactionRepository {

  def findById(id: Long)(implicit executionContext: ExecutionContext): Future[Option[Transaction]]

  def findTransactionsByFilter(transactionFilters: TransactionFilters)(implicit executionContext: ExecutionContext): Future[Seq[TransactionSearchData]]
}

case class TransactionFilters(referenceCode: Option[String], bankNumber: Option[String], establishment: Option[String], bankAgreement: Option[String], bank: Option[String], normalizedStatus: Option[String], status: Option[String], amount: Option[Int])

case class TransactionSearchData(id: Long, referenceCode: String, bankNumber: Option[String], establishment: String, bankAgreement: Option[String], bank: Option[String], status: String, amount: Int)

@Singleton
class TransactionRepositoryImpl extends TransactionRepository {

  override def findById(id: Long)(implicit executionContext: ExecutionContext): Future[Option[Transaction]] = {
    val transactionFilter = Tables.transactions.filter(_.id === id)

    val transactionQuery = for {
      ((((((((t, e), bo), ba), b), n), c), ci), p) <- transactionFilter join Tables.establishments on (_.establishmentId === _.id) join Tables.boletos on (_._1.boletoId === _.id) join Tables.bankAgreements on (_._1._1.bankAgreementId === _.id) join Tables.banks on (_._2.bankId === _.id) join Tables.normalizedStatus on (_._1._1._1._1.normalizedStatusId === _.id) joinLeft Tables.cascadeLogs on (_._1._1._1._1._1.cascadeLogId === _.id) joinLeft Tables.cascadeLogItems on (_._2.map(_.id) === _.cascadeLogId) joinLeft Tables.payments on (_._1._1._1._1._1._1._1.id === _.transactionId)
    } yield (t, e, bo, ba, b, n, c, ci, p)

    db.run(transactionQuery.result)
      .map(mapTableRows)
      .map {
        _.map { tuple =>
          val establishment = new Establishment(tuple._2)
          val boleto = new Boleto(tuple._3)
          val bankAgreement = new BankAgreement(tuple._4, new Bank(tuple._5))
          val normalizedStatus = new NormalizedStatus(tuple._6)
          val cascadeLog = tuple._7.map(new CascadeLog(_, tuple._8.map(new CascadeLogItem(_))))
          val paymentsList = tuple._9.map(new Payment(_))

          new Transaction(tuple._1, establishment, boleto, Option(bankAgreement), Option(normalizedStatus), cascadeLog, paymentsList)
        }
      }
  }


  override def findTransactionsByFilter(transactionFilters: TransactionFilters)(implicit executionContext: ExecutionContext): Future[Seq[TransactionSearchData]] = {
    val filterTransactionByStatus = transactionFilters.status.map(s => Tables.transactions.filter(_.status === s)).getOrElse(Tables.transactions)
    val filterTransaction = transactionFilters.referenceCode.map(r => filterTransactionByStatus.filter(_.referenceCode === r)).getOrElse(filterTransactionByStatus)
    val filterEstablishment = transactionFilters.establishment.map(e => Tables.establishments.filter(_.code === e)).getOrElse(Tables.establishments)
    val filterBoletoByBankNumber = transactionFilters.bankNumber.map(bn => Tables.boletos.filter(_.bankNumber === bn)).getOrElse(Tables.boletos)
    val filterBoleto = transactionFilters.amount.map(a => filterBoletoByBankNumber.filter(_.amount === a)).getOrElse(filterBoletoByBankNumber)
    val filterBankAgreement = transactionFilters.bankAgreement.map(s => Tables.bankAgreements.filter(_.agreementCode === s)).getOrElse(Tables.bankAgreements)
    val filterBank = transactionFilters.bank.map(s => Tables.banks.filter(_.code === s)).getOrElse(Tables.banks)

    val transactionQuery = for {
      ((((t, e), bo), ba), b) <- filterTransaction join filterEstablishment on (_.establishmentId === _.id) join filterBoleto on (_._1.boletoId === _.id) join filterBankAgreement on (_._1._1.bankAgreementId === _.id) join filterBank on (_._2.bankId === _.id)
    } yield (t.id, t.referenceCode, t.status, e.code, bo.bankNumber, bo.amount, ba.agreementCode, b.code)

    db.run(transactionQuery.sortBy(_._1.desc).result)
      .map(_.map(t => TransactionSearchData(t._1, t._2, t._5, t._4, Option(t._7), Option(t._8), t._3, t._6)))
  }

  private def mapTableRows(seq: Seq[(TransactionDB, EstablishmentDB, BoletoDB, BankAgreementDB, BankDB, NormalizedStatusDB, Option[CascadeLogDB], Option[CascadeLogItemDB], Option[PaymentDB])]): Option[(TransactionDB, EstablishmentDB, BoletoDB, BankAgreementDB, BankDB, NormalizedStatusDB, Option[CascadeLogDB], Seq[CascadeLogItemDB], Seq[PaymentDB])] = {
    if (seq.isEmpty) {
      Option.empty
    } else {
      val head = seq.head

      val seqCascadeLogItem = seq.map(_._8).filter(_.isDefined).map(_.get).distinct
      val seqPayment = seq.map(_._9).filter(_.isDefined).map(_.get).distinct

      Option((head._1, head._2, head._3, head._4, head._5, head._6, head._7, seqCascadeLogItem, seqPayment))
    }
  }
}
