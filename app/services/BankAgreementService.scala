package services

import domain.{BankAgreement, Establishment}
import javax.inject.{Inject, Singleton}

import scala.concurrent.{ExecutionContext, Future}

sealed trait BankAgreementService {
  def getAgreementFullData(bankAgreement: BankAgreement)(implicit ec: ExecutionContext): Future[BankAgreementFullData]
}

case class BankAgreementFullData(bankAgreement: BankAgreement, bankNumber: Option[String], establishments: Seq[Establishment])

@Singleton
class BankAgreementServiceImpl @Inject()(establishmentBankAgreementRepository: EstablishmentBankAgreementRepository, bankAgreementRepository: BankAgreementRepository) extends BankAgreementService {

  override def getAgreementFullData(bankAgreement: BankAgreement)(implicit ec: ExecutionContext): Future[BankAgreementFullData] = {
    establishmentBankAgreementRepository.findByBankAgreement(bankAgreement)
      .zip(bankAgreementRepository.findBankNumber(bankAgreement.id))
      .map { result =>
        if (result._1.isEmpty) {
          BankAgreementFullData(bankAgreement, result._2, Seq.empty)
        } else {
          val establishments = result._1.map(_.establishment)
          BankAgreementFullData(bankAgreement, result._2, establishments)
        }
      }
  }
}
