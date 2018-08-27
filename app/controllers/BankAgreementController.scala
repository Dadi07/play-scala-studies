package controllers

import controllers.ResponseWriters.bankAgreementSearchWrites
import javax.inject.{Inject, Singleton}
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}
import services.BankAgreementRepository

import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class BankAgreementController @Inject() (cc: ControllerComponents, bankAgreementRepository: BankAgreementRepository) extends AbstractController(cc){

  def searchBankAgreements(agreementCode: Option[String], bank: Option[String]) = Action.async {
    bankAgreementRepository.findBankAgreementsByFilter(agreementCode, bank)
      .map { seq =>
        if (seq.isEmpty) NotFound else Ok(Json.toJson(seq))
      }
  }
}
