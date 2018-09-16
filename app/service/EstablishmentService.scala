package service

import domain.{BankAgreement, Establishment, Merchant}
import javax.inject.{Inject, Singleton}
import repository.{EstablishmentBankAgreementRepository, EstablishmentRepository, MerchantEstablishmentRepository}

import scala.concurrent.{ExecutionContext, Future}

sealed trait EstablishmentService {
  def getEstablishmentFullData(id: Long)(implicit ec: ExecutionContext): Future[Option[EstablishmentFullData]]
}

case class EstablishmentFullData(establishment: Establishment, merchants: Seq[Merchant], bankAgreements: Seq[BankAgreement])

@Singleton
class EstablishmentServiceImpl @Inject()(establishmentRepository: EstablishmentRepository, merchantEstablishmentRepository: MerchantEstablishmentRepository, establishmentBankAgreementRepository: EstablishmentBankAgreementRepository) extends EstablishmentService {
  override def getEstablishmentFullData(id: Long)(implicit ec: ExecutionContext): Future[Option[EstablishmentFullData]] = {
    establishmentRepository.findById(id)
      .flatMap {
        case None => Future.successful(None)
        case Some(establishment) => merchantEstablishmentRepository.findByEstablishment(establishment)
          .zip(establishmentBankAgreementRepository.findByEstablishment(establishment))
          .map(tuple => Option(EstablishmentFullData(establishment, tuple._1.map(_.merchant), tuple._2.map(_.bankAgreement))))
        }
  }
}
