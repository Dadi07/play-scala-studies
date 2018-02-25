package domain

import java.time.{LocalDate, LocalDateTime}

import slick.lifted.Tag
import slick.jdbc.MySQLProfile.api._
import domain.DomainUtils.localDateConverter
import domain.DomainUtils.localDateTimeConverter

object BoletoTransactionDomain {

  case class TransactionDB(id: Long,
                           referenceCode: String,
                           establishmentId: Long,
                           status: String,
                           paidAmount: Option[Int],
                           paymentDate: Option[LocalDate],
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

  case class PayerDB(documentType: String,
                     documentNumber: String,
                     name: String,
                     street: Option[String],
                     district: Option[String],
                     city: Option[String],
                     state: Option[String],
                     postalCode: Option[String])

  case class RecipientDB(name: Option[String], agreementCode: Option[String], agencyCode: Option[String], documentNumber: Option[String])

  case class FineDB(percentage: Option[Int], interestPercentage: Option[Int], startDate: Option[LocalDate])

  case class BoletoDB(id: Long,
                      barcode: Option[String],
                      paymentCode: Option[String],
                      bankNumber: Option[String],
                      gatewayNumber: Option[String],
                      accept: Option[String],
                      boletoType: String,
                      chargeType: Option[String],
                      amount: Int,
                      amountReduction: Option[Int],
                      fine: FineDB,
                      discountAmount: Option[Int],
                      discountDeadLine: Option[LocalDate],
                      issueDate: LocalDate,
                      registerDate: Option[LocalDate],
                      dueDate: LocalDate,
                      startProtest: Option[LocalDate],
                      expirationDate: Option[LocalDate],
                      message: Option[String],
                      payer: PayerDB,
                      recipient: RecipientDB)

  case class PaymentDB(id: Long,
                       transactionId: Long,
                       amount: Int,
                       nsa: String,
                       nsr: String,
                       paymentDate: LocalDate,
                       creditDate: LocalDate,
                       notificationId: Option[String],
                       creation: LocalDate,
                       updated: LocalDate,
                       deleted: Boolean)

  class TransactionTable(tag: Tag) extends Table[TransactionDB](tag, "boleto_transaction") {

    def id = column[Long]("idt_transaction", O.PrimaryKey, O.AutoInc)

    def referenceCode = column[String]("cod_reference")

    def establishmentId = column[Long]("idt_establishment")

    def status = column[String]("cod_transaction_status")

    def paidAmount = column[Option[Int]]("num_paid_amount")

    def paymentDate = column[Option[LocalDate]]("num_paid_amount")

    def boletoId = column[Long]("idt_boleto")

    def bankId = column[Option[Long]]("idt_bank")

    def nsu = column[String]("cod_nsu")

    def nsuDate = column[LocalDate]("dat_nsu_date")

    def normalizedStatusId = column[Option[Long]]("idt_normalized_status")

    def cascadeLogId = column[Option[Long]]("idt_cascade_log")

    def bankResponseCode = column[Option[String]]("cod_bank_response")

    def bankResponseStatus = column[Option[String]]("cod_bank_response_status")

    def notificationUrl = column[String]("des_notification_url")

    def creation = column[LocalDateTime]("dat_creation")

    def updated = column[LocalDateTime]("dat_update")

    def deleted = column[Boolean]("flg_deleted")

    def * = (id, referenceCode, establishmentId, status, paidAmount, paymentDate, boletoId, bankId, nsu, nsuDate, normalizedStatusId, cascadeLogId, bankResponseCode, bankResponseStatus, notificationUrl, creation, updated, deleted) <> ((TransactionDB.apply _).tupled, TransactionDB.unapply)

    def establishment = foreignKey("esta_boletran_fk", establishmentId, EstablishmentDomain.establishments)(_.id)

    def boleto = foreignKey("bole_boletran_fk", boletoId, boletos)(_.id)

    def bank = foreignKey("bank_boletran_fk", bankId, BankDomain.banks)(_.id)

    def normalizedStatus = foreignKey("normstat_boletran_fk", normalizedStatusId, NormalizedStatusDomain.normalizedStatus)(_.id)

    def cascadeLog = foreignKey("casclog_boletran_fk", cascadeLogId, CascadeLogDomain.cascadeLogs)(_.id)
  }

  class BoletoTable(tag: Tag) extends Table[BoletoDB](tag, "boleto") {
    def id = column[Long]("idt_boleto", O.PrimaryKey, O.AutoInc)

    def barcode = column[Option[String]]("cod_barcode")

    def paymentCode = column[Option[String]]("cod_payment_code")

    def bankNumber = column[Option[String]]("cod_bank_number")

    def accept = column[Option[String]]("cod_accept")

    def gatewayNumber = column[Option[String]]("cod_gateway_number")

    def boletoType = column[String]("cod_boleto_type")

    def chargeType = column[Option[String]]("des_charge_type")

    def amount = column[Int]("num_amount")

    def amountReduction = column[Option[Int]]("num_amount_reduction")

    def finePercentage = column[Option[Int]]("num_fine_percentage")

    def fineInterestPercentage = column[Option[Int]]("num_fine_interest_percentage")

    def startFine = column[Option[LocalDate]]("dat_start_fine")

    def discountAmount = column[Option[Int]]("num_discount_amount")

    def discountDeadline = column[Option[LocalDate]]("dat_discount_deadline")

    def issueDate = column[LocalDate]("dat_issue_date")

    def registrationDate = column[Option[LocalDate]]("dat_registration_date")

    def dueDate = column[LocalDate]("dat_due_date")

    def startProtest = column[Option[LocalDate]]("dat_start_protest")

    def expiration = column[Option[LocalDate]]("dat_expiration")

    def message = column[Option[String]]("des_boleto_message")

    def payerDocumentType = column[String]("cod_payer_document_type")

    def payerDocumentNumber = column[String]("num_payer_document")

    def payerName = column[String]("nam_payer")

    def payerStreet = column[Option[String]]("des_payer_street")

    def payerCity = column[Option[String]]("des_payer_city")

    def payerDistrict = column[Option[String]]("des_payer_district")

    def payerState = column[Option[String]]("des_payer_state")

    def payerPostalCode = column[Option[String]]("num_payer_postal_code")

    def recipientName = column[Option[String]]("nam_recipient")

    def recipientAgreement = column[Option[String]]("cod_recipient_agreement")

    def recipientAgency = column[Option[String]]("cod_recipient_agency")

    def recipientDocumentNumber = column[Option[String]]("num_recipient_document")


    def fineProjection = (finePercentage, fineInterestPercentage, startFine) <> ((FineDB.apply _).tupled, FineDB.unapply)

    def payerProjection = (payerDocumentType, payerDocumentNumber, payerName, payerStreet, payerDistrict, payerCity, payerState, payerPostalCode) <> ((PayerDB.apply _).tupled, PayerDB.unapply)

    def recipientProjection = (recipientName, recipientAgreement, recipientAgency, recipientDocumentNumber) <> ((RecipientDB.apply _).tupled, RecipientDB.unapply)

    def * = (id, barcode, paymentCode, bankNumber, gatewayNumber, accept, boletoType, chargeType, amount, amountReduction, fineProjection, discountAmount, discountDeadline, issueDate, registrationDate, dueDate, startProtest, expiration, message, payerProjection, recipientProjection) <> ((BoletoDB.apply _).tupled, BoletoDB.unapply)
  }

  class PaymentTable(tag: Tag) extends Table[PaymentDB](tag, "payment") {
    def id = column[Long]("idt_payment", O.PrimaryKey, O.AutoInc)
    def transactionId = column[Long]("idt_transaction")
    def amount = column[Int]("num_paid_amount")
    def nsa = column[String]("cod_nsa")
    def nsr = column[String]("cod_nsr")
    def paymentDate = column[LocalDate]("dat_payment")
    def creditDate = column[LocalDate]("dat_credit")
    def notificationId = column[Option[String]]("cod_notification_id")
    def creation = column[LocalDate]("dat_creation")
    def updated = column[LocalDate]("dat_update")
    def deleted = column[Boolean]("flg_deleted")

    override def * = (id, transactionId, amount, nsa, nsr, paymentDate, creditDate, notificationId, creation, updated, deleted) <> ((PaymentDB.apply _).tupled, PaymentDB.unapply)

    def transaction = foreignKey("boletran_pay_fk", transactionId, transactions)(_.id)
  }

  val payments = TableQuery[PaymentTable]
  val boletos = TableQuery[BoletoTable]
  val transactions = TableQuery[TransactionTable]

}