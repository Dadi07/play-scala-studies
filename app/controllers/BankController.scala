package controllers

import controllers.ResponseWriters.bankWrites
import javax.inject.Inject
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}
import services.BankRepository

import scala.concurrent.ExecutionContext.Implicits.global

class BankController @Inject()(cc: ControllerComponents, bankRepository: BankRepository) extends AbstractController(cc) {

  def searchBank(code: Option[String]) = Action.async {
    bankRepository.findByCode(code)
        .map {
          case Nil => NotFound
          case banks => Ok(Json.toJson(banks))
        }
  }
}
