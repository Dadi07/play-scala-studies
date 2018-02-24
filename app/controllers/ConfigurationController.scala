package controllers

import javax.inject.Inject

import domain.ResponseWriters.ConfigurationWrites
import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}
import services.ConfigurationService

import scala.concurrent.ExecutionContext.Implicits.global

class ConfigurationController @Inject()(cc: ControllerComponents, configurationService: ConfigurationService) extends AbstractController(cc) {

  def createConfiguration(key: String, value: String) = Action {
    Created
  }

  def searchConfiguration(key: String) = Action.async {
    Logger.info(s"Request for configuration with key=$key")

    configurationService.getConfiguration(key)
      .map { config =>
        val value = config.value
        Logger.info(s"Response for configuration with key=$key value=$value")

        Ok(Json.toJson(config))
      }
  }

  def changeConfiguration(key: String, value: String) = Action {
    Ok
  }

  def deleteConfiguration(key: String) = Action {
    Ok
  }
}
