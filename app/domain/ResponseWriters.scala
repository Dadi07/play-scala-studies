package domain

import domain.BoletoGatewayDomain.Configuration
import play.api.libs.json.{JsValue, Json, Writes}

object ResponseWriters {
  implicit val ConfigurationWrites = new Writes[Configuration] {
    override def writes(config: Configuration): JsValue = {
      Json.obj("id" -> config.id,
        "key" -> config.key,
        "value" -> config.value)
    }
  }
}
