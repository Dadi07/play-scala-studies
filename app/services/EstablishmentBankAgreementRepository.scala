package services

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
    val dbioAction = Tables.establishmentBankAgreements.filter(_.establishmentId === establishment.id).result

    return RepositoryUtils.db.run(dbioAction)
        .map { seq =>
          seq.map( e => new EstablishmentBankAgreement(e.id, establishment, ))
        }
  }

  override def findByBankAgreement(bankAgreement: BankAgreement)(implicit ec: ExecutionContext): Future[Seq[EstablishmentBankAgreement]] = {
    val baseQuery = Tables.establishmentBankAgreements.filter(_.bankAgreementId === bankAgreement.id)

    val dbioAction = for {
      (eba, e)
    } yield (eba, e)

    return RepositoryUtils.db.run(dbioAction)
      .map { seq =>
        seq.map( e => new EstablishmentBankAgreement(e.id, e, bankAgreement))
      }
  }
}
