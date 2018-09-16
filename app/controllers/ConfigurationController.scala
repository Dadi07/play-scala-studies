package controllers

import controllers.ResponseWriters.configurationWrites
import domain.Configuration
import javax.inject.{Inject, Singleton}
import play.api.Logger
import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import play.api.mvc.{AbstractController, ControllerComponents, Request}
import repository.ConfigurationRepository

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class ConfigurationController @Inject()(cc: ControllerComponents, configurationRepository: ConfigurationRepository) extends AbstractController(cc) {

  implicit val configurationReads = Json.reads[Configuration]

  def searchConfiguration(key: Option[String]) = Action.async {
    Logger.info(s"action=endpoint-request uri=/configurations method=GET params={key=$key}")

    configurationRepository.findByKey(key)
      .map {
        case Nil => NotFound
        case configs =>
          Logger.info(s"Response for configuration with key=$key")
          Ok(Json.toJson(configs))
      }
  }

  def changeConfiguration(key: String) = Action.async(parse.tolerantJson) { request: Request[JsValue] =>
     request.body.validate[Seq[String]] match {
       case JsSuccess(values, _) =>
         Logger.info(values.toString())
         Future(Ok)
       case JsError(erros) =>
         Logger.info(erros.toString())
         Future(BadRequest)
     }

//     val key = (jsonBody \ "key").get.as[String]
//     val value = (jsonBody \ "value").get.as[String]
//
//     Logger.info(s"action=endpoint-request uri=/configurations/$id method=PUT request=$jsonBody")
//
//     val newConfiguration = Configuration(id, key, value)
//
//     configurationRepository.updateConfiguration(newConfiguration)
//       .map {
//         case 1 => Ok(Json.toJson(newConfiguration))
//         case 0 => NotFound
//       }
   }
}
