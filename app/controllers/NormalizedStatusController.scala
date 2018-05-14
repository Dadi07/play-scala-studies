package controllers

import controllers.ResponseWriters.normalizedStatusWrites
import domain.BankResponseStatus
import javax.inject.{Inject, Singleton}
import play.api.libs.json.{JsValue, Json, Writes}
import play.api.mvc.{AbstractController, ControllerComponents}
import services.{NormalizedStatusFullData, NormalizedStatusRepository}

import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class NormalizedStatusController @Inject()(cc: ControllerComponents, normalizedStatusRepository: NormalizedStatusRepository) extends AbstractController(cc) {

  implicit val bankResponseStatusWrites = new Writes[BankResponseStatus] {
    override def writes(b: BankResponseStatus): JsValue = {
      Json.obj("code" -> b.code,
        "message" -> b.message,
        "isInternalError" -> b.internalError,
        "bank" -> b.bank.name)
    }
  }

  implicit val normalizedStatusFullDataWrites = new Writes[NormalizedStatusFullData] {
    override def writes(n: NormalizedStatusFullData): JsValue = {
      Json.obj("id" -> n.normalizedStatus.id,
        "code" -> n.normalizedStatus.code,
        "message" -> n.normalizedStatus.message,
        "bankResponses" -> n.bankResponses)
    }
  }

  def searchNormalizedStatus(code: Option[String]) = Action.async {
    normalizedStatusRepository.findByCode(code)
      .map {
        case Nil => NotFound
        case normalizedStatus => Ok(Json.toJson(normalizedStatus))
      }
  }

  def searchFullNormalizedStatus(id: Long) = Action.async {
    normalizedStatusRepository.findById(id)
      .map {
        case None => NotFound
        case Some(normalizedStatus) => Ok(Json.toJson(normalizedStatus))
      }
  }
}
