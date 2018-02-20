package domain

import java.time.{LocalDate, LocalDateTime}

import slick.lifted.Tag
import slick.jdbc.MySQLProfile.api._
import domain.DomainUtils.localDateConverter
import domain.DomainUtils.localDateTimeConverter

object BoletoTransactionDomain {

  case class BoletoTransactionDB(id: Long,
                                 referenceCode: String,
                                 establishmentId: Long,
                                 status: String,
                                 paidAmount: Option[Int],
                                 boletoId: Long,
                                 bankId: Option[Long],
                                 nsu: String,
                                 nsuDate: LocalDate,
                                 normalizedStatusId: Option[Long],
                                 cascadeLogId: Option[Long],
                                 bankResponseCode: Option[String],
                                 bankResponseStatus: Option[String],
                                 notificationUrl: String,
                                 creation: LocalDateTime,
                                 updated: LocalDateTime,
                                 deleted: Boolean)

  case class Payer(documentType: String,
                   documentNumber: String,
                   name: String,
                   street: String,
                   district: String,
                   city: String,
                   state: String,
                   postalCode: String)

  case class Recipient(name: String, agreementCode: String, agencyCode: String, documentNumber: String)

  case class FineDB(percentage: Int, interestPercentage: Int, startDate: LocalDate)

  case class BoletoDB(id: Long,
                      barcode: String,
                      paymentCode: String,
                      bankNumber: String,
                      gatewayNumber: String,
                      accept: String,
                      boletoType: String,
                      chargeType: String,
                      amount: Int,
                      amountReduction: Int,
                      fine: FineDB,
                      discountAmount: Int,
                      discountDeadLine: LocalDate,
                      issueDate: LocalDate,
                      registerDate: LocalDate,
                      dueDate: LocalDate,
                      startProtest: LocalDate,
                      expirationDate: LocalDate,
                      message: String,
                      payer: Payer,
                      recipient: Recipient)

  class BoletoTransactionTable(tag: Tag) extends Table[BoletoTransactionDB](tag, "boleto_transaction") {

    def id = column[Long]("idt_transaction", O.PrimaryKey, O.AutoInc)

    def referenceCode = column[String]("cod_reference")

    def establishmentId = column[Long]("idt_establishment")

    def status = column[String]("cod_transaction_status")

    def paidAmount = column[Option[Int]]("num_paid_amount")

    def boletoId = column[Long]("idt_boleto")

    def bankId = column[Option[Long]]("idt_bank")

    def nsu = column[String]("cod_nsu")

    def nsuDate = column[LocalDate]("dat_nsu_date")

    def normalizedStatusId = column[Option[Long]]("idt_normalized_status")

    def cascadeLogId = column[Option[Long]]("idt_cascade_log")

    def bankResponse = column[Option[String]]("cod_bank_response")

    def bankResponseStatus = column[Option[String]]("cod_bank_response_status")

    def notificationUrl = column[String]("des_notification_url")

    def creation = column[LocalDateTime]("dat_creation")

    def update = column[LocalDateTime]("dat_update")

    def deleted = column[Boolean]("flg_deleted")

    def * = (id, referenceCode, establishmentId, status, paidAmount, boletoId, bankId, nsu, nsuDate, normalizedStatusId, cascadeLogId, bankResponse, bankResponseStatus, notificationUrl, creation, update, deleted) <> ((BoletoTransactionDB.apply _).tupled, BoletoTransactionDB.unapply)

    def establishment = foreignKey("esta_boletran_fk", establishmentId, EstablishmentDomain.establishments)(_.id)

    def boleto = foreignKey("bole_boletran_fk", boletoId, boletos)(_.id)

    def bank = foreignKey("bank_boletran_fk", bankId, BankDomain.banks)(_.id)

    def normalizedStatus = foreignKey("normstat_boletran_fk", normalizedStatusId, NormalizedStatusDomain.normalizedStatus)(_.id)

    def cascadeLog = foreignKey("casclog_boletran_fk", cascadeLogId, CascadeLogDomain.cascadeLogs)(_.id)
  }

  class BoletoTable(tag: Tag) extends Table[BoletoDB](tag, "boleto") {
    def id = column[Long]("idt_boleto", O.PrimaryKey, O.AutoInc)

    def barcode = column[String]("cod_barcode")

    def paymentCode = column[String]("cod_payment_code")

    def bankNumber = column[String]("cod_bank_number")

    def accept = column[String]("cod_accept")

    def gatewayNumber = column[String]("cod_gateway_number")

    def boletoType = column[String]("cod_boleto_type")

    def chargeType = column[String]("des_charge_type")

    def amount = column[Int]("num_amount")

    def amountReduction = column[Int]("num_amount_reduction")

    def finePercentage = column[Int]("num_fine_percentage")

    def fineInterestPercentage = column[Int]("num_fine_interest_percentage")

    def startFine = column[LocalDate]("dat_start_fine")

    def discountAmount = column[Int]("num_discount_amount")

    def discountDeadline = column[LocalDate]("dat_discount_deadline")

    def issueDate = column[LocalDate]("dat_issue_date")

    def registrationDate = column[LocalDate]("dat_registration_date")

    def dueDate = column[LocalDate]("dat_due_date")

    def startProtest = column[LocalDate]("dat_start_protest")

    def expiration = column[LocalDate]("dat_expiration")

    def message = column[String]("des_boleto_message")

    def payerDocumentType = column[String]("cod_payer_document_type")

    def payerDocumentNumber = column[String]("num_payer_document")

    def payerName = column[String]("nam_payer")

    def payerStreet = column[String]("des_payer_street")

    def payerCity = column[String]("des_payer_city")

    def payerDistrict = column[String]("des_payer_district")

    def payerState = column[String]("des_payer_state")

    def payerPostalCode = column[String]("num_payer_postal_code")

    def recipientName = column[String]("nam_recipient")

    def recipientAgreement = column[String]("cod_recipient_agreement")

    def recipientAgency = column[String]("cod_recipient_agency")

    def recipientDocumentNumber = column[String]("num_recipient_document")


    def fineProjection = (finePercentage, fineInterestPercentage, startFine) <> ((FineDB.apply _).tupled, FineDB.unapply)

    def payerProjection = (payerDocumentType, payerDocumentNumber, payerName, payerStreet, payerDistrict, payerCity, payerState, payerPostalCode) <> ((Payer.apply _).tupled, Payer.unapply)

    def recipientProjection = (recipientName, recipientAgreement, recipientAgency, recipientDocumentNumber) <> ((Recipient.apply _).tupled, Recipient.unapply)

    def * = (id, barcode, paymentCode, bankNumber, gatewayNumber, accept, boletoType, chargeType, amount, amountReduction, fineProjection, discountAmount, discountDeadline, issueDate, registrationDate, dueDate, startProtest, expiration, message, payerProjection, recipientProjection) <> ((BoletoDB.apply _).tupled, BoletoDB.unapply)
  }

  val boletos = TableQuery[BoletoTable]
  val transactions = TableQuery[BoletoTransactionTable]

}