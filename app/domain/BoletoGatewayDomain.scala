package domain

import java.time.{LocalDate, LocalDateTime}

import domain.BankDomain.BankDB
import domain.EstablishmentDomain.EstablishmentDB
import domain.NormalizedStatusDomain.NormalizedStatusDB

object BoletoGatewayDomain {

  object TransactionStatus extends Enumeration {

    protected case class Val(code: String) extends super.Val

    type TransactionStatus = Val

    val CREATED = Val("created")
    val REGISTERED = Val("registered")
    val PAID = Val("paid")
    val PARTIALLY_PAID = Val("partially_paid")
    val UNKNOWN = Val("unknown")
  }

  case class Establishment(id: Long, name: String, code: String) {
    def this(e: EstablishmentDB) = this(e.id, e.name, e.code)
  }

  case class Bank(code: String, name: String) {
    def this(b: BankDB) = this(b.code, b.name)
  }

  case class NormalizedStatus(code: String, message: String) {
    def this(n: NormalizedStatusDB) = this(n.code, n.message)
  }

  case class CascadeLog(rule: String, items: Seq[CascadeLogItem])

  case class CascadeLogItem(bank: String, responseCode: Option[String], responseMessage: Option[String], exception: Option[String], creation: LocalDateTime)

  case class Fine(percentage: Option[Int], interestPercentage: Option[Int], startDate: LocalDate)

  case class Discount(amount: Int, deadline: LocalDate)

  case class Payer(documentType: String,
                   documentNumber: String,
                   name: String,
                   street: String,
                   district: String,
                   city: String,
                   state: String,
                   postalCode: String)

  case class Recipient(name: String, agreementCode: String, agencyCode: String, documentNumber: String)

  case class Boleto(barcode: Option[String],
                    paymentCode: Option[String],
                    bankNumber: Option[String],
                    gatewayNumber: Option[String],
                    accept: Option[String],
                    boletoType: String, // TODO ENUM
                    chargeType: Option[String],
                    amount: Int,
                    reduction: Option[Int],
                    fine: Option[Fine],
                    discount: Option[Discount],
                    issueDate: LocalDate,
                    registerDate: Option[LocalDate],
                    dueDate: LocalDate,
                    startProtest: Option[LocalDate],
                    expirationDate: Option[LocalDate],
                    message: Option[String],
                    payer: Payer,
                    recipient: Option[Recipient])

  case class Transaction(id: Long,
                         referenceCode: String,
                         establishment: Establishment,
                         status: String, // TODO ENUM
                         paidAmount: Option[Int],
                         paymentDate: Option[LocalDate],
                         boleto: Boleto,
                         bank: Option[Bank],
                         nsu: String,
                         nsuDate: LocalDate,
                         normalizedStatus: Option[NormalizedStatus],
                         cascadeLog: Option[CascadeLog],
                         bankResponseCode: Option[String],
                         bankResponseStatus: Option[String],
                         notificationUrl: String,
                         creation: LocalDateTime,
                         updated: LocalDateTime,
                         deleted: Boolean)

  case class Configuration(id: Long = 0, key: String, value: String)

}
