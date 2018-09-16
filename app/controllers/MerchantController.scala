package controllers

import controllers.ResponseWriters.merchantWrites
import javax.inject.Inject
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}
import repository.MerchantRepository

import scala.concurrent.ExecutionContext.Implicits.global

class MerchantController @Inject()(cc: ControllerComponents, merchantRepository: MerchantRepository) extends AbstractController(cc) {

  def searchMerchant(code: Option[String]) = Action.async {
    merchantRepository.findByCode(code)
        .map {
          case Nil => NotFound
          case merchants => Ok(Json.toJson(merchants))
        }
  }
}
