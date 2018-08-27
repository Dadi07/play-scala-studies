package controllers

import controllers.ResponseWriters.{bankAgreementFullDataWrites, bankAgreementSearchWrites}
import javax.inject.{Inject, Singleton}
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}
import services.{BankAgreementRepository, BankAgreementService}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class BankAgreementController @Inject() (cc: ControllerComponents, bankAgreementRepository: BankAgreementRepository, bankAgreementService: BankAgreementService) extends AbstractController(cc){


  def searchBankAgreement(id: Long) = Action.async {
    bankAgreementRepository.findById(id)
      .flatMap { bankAgreement =>
        bankAgreement.map { ba =>
          bankAgreementService.getAgreementFullData(ba)
            .map { f =>
              Ok(Json.toJson(f))
            }
        }.getOrElse(Future(NotFound))
      }
  }

  def searchBankAgreements(agreementCode: Option[String], bank: Option[String]) = Action.async {
    bankAgreementRepository.findBankAgreementsByFilter(agreementCode, bank)
      .map { seq =>
        if (seq.isEmpty) NotFound else Ok(Json.toJson(seq))
      }
  }
}
