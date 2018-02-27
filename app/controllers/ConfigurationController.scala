package controllers

import javax.inject.{Inject, Singleton}

import domain.BoletoGatewayDomain.Configuration
import ResponseWriters.configurationWrites
import play.api.Logger
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{AbstractController, ControllerComponents, Request}
import services.ConfigurationService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

@Singleton
class ConfigurationController @Inject()(cc: ControllerComponents, configurationService: ConfigurationService) extends AbstractController(cc) {

  def createConfiguration() = Action.async(parse.tolerantJson) { request: Request[JsValue] =>
    val jsonBody = request.body

    val key = (jsonBody \ "key").get.as[String]
    val value = (jsonBody \ "value").get.as[String]

    Logger.info(s"action=endpoint-request uri=/configurations method=POST request=$jsonBody")

    val newConfiguration = Configuration(key = key, value = value)

    val futureCreate = configurationService.createConfiguration(newConfiguration)

    futureCreate.onComplete {
      case Success(id) => Logger.info(s"Response created configuration with key=$key")
      case Failure(t) => Logger.error(s"Response creation error for configuration with key=$key with $t")
    }

    futureCreate.map { id =>
      Created(Json.toJson(Configuration(id, key, value)))
    }
  }

  def searchConfiguration(key: Option[String]) = Action.async {
    Logger.info(s"action=endpoint-request uri=/configurations method=GET params={key=$key}")

    configurationService.getConfigurations(key)
      .map { configs =>
        Logger.info(s"Response for configuration with key=$key")

        Ok(Json.toJson(configs))
      }
  }

  def changeConfiguration(id: Long) = Action.async(parse.tolerantJson) { request: Request[JsValue] =>
    val jsonBody = request.body

    val key = (jsonBody \ "key").get.as[String]
    val value = (jsonBody \ "value").get.as[String]

    Logger.info(s"action=endpoint-request uri=/configurations/$id method=PUT request=$jsonBody")

    val newConfiguration = Configuration(id, key, value)

    configurationService.updateConfiguration(newConfiguration)
      .map {
        case 1 => Ok(Json.toJson(newConfiguration))
        case 0 => NotFound
      }
  }

  def removeConfiguration(id: Long) = Action.async {
    Logger.info(s"action=endpoint-request uri=/configurations/$id method=DELETE")

    configurationService.deleteConfiguration(id)
      .map {
        case 1 => NoContent
        case 0 => NotFound
      }
  }
}
