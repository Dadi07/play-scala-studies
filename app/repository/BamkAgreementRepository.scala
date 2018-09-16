package repository

import domain._
import javax.inject.Singleton
import repository.RepositoryUtils.db
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.{ExecutionContext, Future}

sealed trait BankAgreementRepository {

  def findById(id: Long)(implicit executionContext: ExecutionContext): Future[Option[BankAgreement]]

  def findBankNumber(agreementId: Long)(implicit executionContext: ExecutionContext): Future[Option[String]]

  def findBankAgreementsByFilter(agreementCode: Option[String], bank: Option[String])(implicit executionContext: ExecutionContext): Future[Seq[BankAgreementSearchData]]
}

case class BankAgreementSearchData(id: Long, agreementCode: String, bank: String, companyName: String)

@Singleton
class BankAgreementRepositoryImpl extends BankAgreementRepository {

  override def findById(id: Long)(implicit executionContext: ExecutionContext): Future[Option[BankAgreement]] = {
    val bankAgreementFilter = Tables.bankAgreements.filter(_.id === id)

    val bankAgreementQuery = for {
      (ba, b) <- bankAgreementFilter join Tables.banks on (_.bankId === _.id)
    } yield (ba, b)

    db.run(bankAgreementQuery.result)
      .map { seq =>
        if(seq.isEmpty) {
          Option.empty
        } else {
          Option(new BankAgreement(seq.head._1, new Bank(seq.head._2)))
        }
      }
  }


  override def findBankAgreementsByFilter(agreementCode: Option[String], bank: Option[String])(implicit executionContext: ExecutionContext): Future[Seq[BankAgreementSearchData]] = {
    val filterBankAgreement = agreementCode.map(c => Tables.bankAgreements.filter(_.agreementCode === c)).getOrElse(Tables.bankAgreements)
    val filterBank = bank.map(b => Tables.banks.filter(_.code === b)).getOrElse(Tables.banks)

    val bankAgreementQuery = for {
      (ba, b) <- filterBankAgreement join filterBank on (_.bankId === _.id)
    } yield (ba, b)

    db.run(bankAgreementQuery.result)
      .map(_.map(result => BankAgreementSearchData(result._1.id, result._1.agreementCode, result._2.code, result._1.companyName)))
  }

  override def findBankNumber(agreementId: Long)(implicit executionContext: ExecutionContext): Future[Option[String]] = {
    val query = Tables.documenNumbers.filter(_.bankAgreementId === agreementId).result

    db.run(query)
      .map { seq =>
        if (seq.isEmpty) Option.empty else Option(seq.head.documenNumber)
      }
  }
}
