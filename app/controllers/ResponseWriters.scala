package controllers

import domain.BoletoGatewayDomain.{Configuration, Transaction}
import play.api.libs.json.{JsValue, Json, Writes}

object ResponseWriters {
  implicit val ConfigurationWrites = new Writes[Configuration] {
    override def writes(config: Configuration): JsValue = {
      Json.obj("id" -> config.id,
        "key" -> config.key,
        "value" -> config.value)
    }
  }

  implicit val TransactionWrites = new Writes[Transaction] {
    override def writes(t: Transaction): JsValue = {
      Json.obj("id" -> t.id,
        "reference_code" -> t.referenceCode,
        "establishment" -> t.establishment.code,
        "status" -> Json.obj("code" -> t.normalizedStatus.get.code,
          "message" -> t.normalizedStatus.get.message),
        "bank" -> Json.obj("name" -> t.bank.get.code,
          "response_code" -> t.bankResponseCode,
          "response_message" -> t.bankResponseStatus),
        "boleto" -> Json.obj("barcode" -> t.boleto.barcode,
          "payment_code" -> t.boleto.paymentCode,
          "bank_number" -> t.boleto.bankNumber,
          "amount" -> t.boleto.amount,
          "payer" -> Json.obj("name" -> t.boleto.payer.name,
            "document" -> Json.obj("type" -> t.boleto.payer.documentType,
              "number" -> t.boleto.payer.documentNumber))),
        "cascade" -> Json.obj("rule" -> t.cascadeLog.get.rule),
        "notification_url" -> t.notificationUrl,
        "creation" -> t.creation,
        "updated" -> t.updated)
    }
  }
}
