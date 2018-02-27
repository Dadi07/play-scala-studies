package controllers

import domain.BoletoGatewayDomain._
import play.api.libs.json.{JsPath, JsValue, Json, Writes}
import play.api.libs.functional.syntax._

object ResponseWriters {
  implicit val configurationWrites = new Writes[Configuration] {
    override def writes(config: Configuration): JsValue = {
      Json.obj("id" -> config.id,
        "key" -> config.key,
        "value" -> config.value)
    }
  }

  implicit val establishmentWrites = new Writes[Establishment] {
    override def writes(e: Establishment): JsValue = {
      Json.obj("code" -> e.code,
        "name" -> e.name)
    }
  }

  implicit val fineWrites = new Writes[Fine] {
    override def writes(f: Fine): JsValue = {
      Json.obj("percentage" -> f.percentage,
        "interest_percentage" -> f.interestPercentage,
        "start" -> f.startDate)
    }
  }

  implicit val discountWrites = new Writes[Discount] {
    override def writes(d: Discount): JsValue = {
      Json.obj("amount" -> d.amount,
        "deadline" -> d.deadline)
    }
  }

  implicit val payerWrites = new Writes[Payer] {
    override def writes(p: Payer): JsValue = {
      Json.obj("name" -> p.name,
        "document" -> Json.obj("type" -> p.documentType,
          "number" -> p.documentNumber),
        "Address" -> Json.obj("street" -> p.street,
          "district" -> p.district,
          "city" -> p.city,
          "state" -> p.state,
          "postal_code" -> p.postalCode))
    }
  }

  implicit val recipientWrites = new Writes[Recipient] {
    override def writes(r: Recipient): JsValue = {
      Json.obj("name" -> r.name,
        "document_number" -> r.documentNumber,
        "agreement" -> r.agreementCode,
        "agency" -> r.agencyCode)
    }
  }

  implicit val boletoWrites = new Writes[Boleto] {
    override def writes(b: Boleto): JsValue = {
      Json.obj("barcode" -> b.barcode,
        "payment_code" -> b.paymentCode,
        "bank_number" -> b.bankNumber,
        "gateway_number" -> b.gatewayNumber,
        "accept" -> b.accept,
        "type" -> b.boletoType,
        "charge_type" -> b.chargeType,
        "amount" -> b.amount,
        "reduction" -> b.reduction,
        "fine" -> b.fine,
        "discount" -> b.discount,
        "issue_date" -> b.issueDate,
        "register_date" -> b.registerDate,
        "due_date" -> b.dueDate,
        "start_protest" -> b.startProtest,
        "expiration_date" -> b.expirationDate,
        "message" -> b.message,
        "payer" -> b.payer,
        "recipient" -> b.recipient)
    }
  }

  implicit val normalizedStatusWrites = new Writes[NormalizedStatus] {
    override def writes(n: NormalizedStatus): JsValue = {
      Json.obj("code" -> n.code,
        "message" -> n.message)
    }
  }

  implicit val cascadeLogItemWrites = new Writes[CascadeLogItem] {
    override def writes(c: CascadeLogItem): JsValue = {
      Json.obj("bank" -> c.bank,
        "response_code" -> c.responseCode,
        "response_message" -> c.responseMessage,
        "exception" -> c.exception,
        "creation" -> c.creation)
    }
  }

  implicit val cascadeLogWrites = new Writes[CascadeLog] {
    override def writes(c: CascadeLog): JsValue = {
      Json.obj("rule" -> c.rule,
        "attempts" -> c.items)
    }
  }

  implicit val paymentWrites = new Writes[Payment] {
    override def writes(p: Payment): JsValue = {
      Json.obj("amount" -> p.amount,
        "payment_date" -> p.paymentDate,
        "credit_date" -> p.creditDate,
        "nsa" -> p.nsa,
        "nsr" -> p.nsr,
        "payment_hash" -> p.notificationId,
        "creation" -> p.creation)
    }
  }

  implicit val transactionWrites = new Writes[Transaction] {
    override def writes(t: Transaction): JsValue = {
      Json.obj("id" -> t.id,
        "reference_code" -> t.referenceCode,
        "establishment" -> t.establishment,
        "status" -> t.normalizedStatus,
        "bank" -> Json.obj("name" -> t.bank.get.code,
          "response" -> Json.obj("code" -> t.bankResponseCode,
            "message" -> t.bankResponseStatus)),
        "boleto" -> t.boleto,
        "payments" -> t.payments,
        "cascade" -> t.cascadeLog,
        "notification_url" -> t.notificationUrl,
        "creation" -> t.creation,
        "updated" -> t.updated)
    }
  }
}
