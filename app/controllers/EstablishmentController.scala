package controllers

import controllers.ResponseWriters.establishmentWrites
import domain.{BankAgreement, Merchant}
import javax.inject.Inject
import play.api.libs.json.{JsValue, Json, Writes}
import play.api.mvc.{AbstractController, ControllerComponents}
import repository.EstablishmentRepository
import service.{EstablishmentFullData, EstablishmentService}

import scala.concurrent.ExecutionContext.Implicits.global

class EstablishmentController @Inject()(cc: ControllerComponents, establishmentRepository: EstablishmentRepository, establishmentService: EstablishmentService) extends AbstractController(cc) {

  implicit val merchantStatusWrites = new Writes[Merchant] {
    override def writes(m: Merchant): JsValue = {
      Json.obj("code" -> m.code,
        "name" -> m.name)
    }
  }

  implicit val bankAgreementWrites = new Writes[BankAgreement] {
    override def writes(b: BankAgreement): JsValue = {
      Json.obj("agreement_code" -> b.code,
        "company_name" -> b.companyName,
        "bank" -> b.bank.code)
    }
  }

  implicit val establishmentFullDataWrites = new Writes[EstablishmentFullData] {
    override def writes(e: EstablishmentFullData): JsValue = {
      Json.obj("id" -> e.establishment.id,
        "code" -> e.establishment.code,
        "name" -> e.establishment.name,
        "merchants" -> e.merchants,
        "bankAgreements" -> e.bankAgreements)
    }
  }

  def searchEstablishments(establishmentCode: Option[String], merchantCode: Option[String]) = Action.async {
    establishmentRepository.findByFilter(establishmentCode, merchantCode)
        .map {
          case Nil => NotFound
          case merchants => Ok(Json.toJson(merchants))
        }
  }

  def searchEstablishment(id: Long) = Action.async {
    establishmentService.getEstablishmentFullData(id)
      .map {
        case None => NotFound
        case Some(establishment) => Ok(Json.toJson(establishment))
      }
  }
}
