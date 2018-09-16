package repository

import domain._
import javax.inject.Singleton
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.{ExecutionContext, Future}

sealed trait EstablishmentBankAgreementRepository {
  def findByEstablishment(establishment: Establishment)(implicit ec: ExecutionContext): Future[Seq[EstablishmentBankAgreement]]
  def findByBankAgreement(bankAgreement: BankAgreement)(implicit ec: ExecutionContext): Future[Seq[EstablishmentBankAgreement]]
}

@Singleton
class EstablishmentBankAgreementRepositoryImpl extends EstablishmentBankAgreementRepository {
  override def findByEstablishment(establishment: Establishment)(implicit ec: ExecutionContext): Future[Seq[EstablishmentBankAgreement]] = {
    val establishmentBankAgreementFilter = Tables.establishmentBankAgreements.filter(_.establishmentId === establishment.id)

    val dbioAction = for {
      ((eba, ba), b) <- establishmentBankAgreementFilter join Tables.bankAgreements on (_.bankAgreementId === _.id) join Tables.banks on (_._2.bankId === _.id)
    } yield (eba, ba, b)

    RepositoryUtils.db.run(dbioAction.result)
      .map(_.map(result => EstablishmentBankAgreement(result._1.id, establishment, new BankAgreement(result._2, new Bank(result._3)))))
  }

  override def findByBankAgreement(bankAgreement: BankAgreement)(implicit ec: ExecutionContext): Future[Seq[EstablishmentBankAgreement]] = {
    val establishmentBankAgreementFilter = Tables.establishmentBankAgreements.filter(_.bankAgreementId === bankAgreement.id)

    val dbioAction = for {
      (eba, e) <- establishmentBankAgreementFilter join Tables.establishments on (_.establishmentId === _.id)
    } yield (eba, e)

    RepositoryUtils.db.run(dbioAction.result)
      .map(_.map(result => EstablishmentBankAgreement(result._1.id, new Establishment(result._2), bankAgreement)))
  }
}
