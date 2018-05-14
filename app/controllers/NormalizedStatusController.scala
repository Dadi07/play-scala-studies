package controllers

import controllers.ResponseWriters.{normalizedStatusFullDataWrites, normalizedStatusWrites}
import javax.inject.{Inject, Singleton}
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}
import services.NormalizedStatusRepository

import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class NormalizedStatusController @Inject()(cc: ControllerComponents, normalizedStatusRepository: NormalizedStatusRepository) extends AbstractController(cc) {

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
