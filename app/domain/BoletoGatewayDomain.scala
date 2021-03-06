package domain

import java.time.{LocalDate, LocalDateTime}

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

case class Merchant(id: Long, name: String, code: String, password: String) {
  def this(m: MerchantDB) = this(m.id, m.name, m.code, m.password)
}

case class MerchantEstablishment(id: Long, merchant: Merchant, establishment: Establishment) {
  def this(me: MerchantEstablishmentDB, merchant: Merchant, establishment: Establishment) = this(me.id, merchant, establishment)
}

case class Bank(id: Long, code: String, name: String) {
  def this(b: BankDB) = this(b.id, b.code, b.name)
}

case class BankAgreement(id: Long, code: String, companyName: String, agency: String, documentNumber: String, bank: Bank) {
  def this(b: BankAgreementDB, bank: Bank) = this(b.id, b.agreementCode, b.companyName, b.agency, b.documentNumber, bank)
}

case class EstablishmentBankAgreement(id: Long, establishment: Establishment, bankAgreement: BankAgreement) {
  def this(e: EstablishmentBankAgreementDB, establishment: Establishment, bankAgreement: BankAgreement) = this(e.id, establishment, bankAgreement)
}

case class NormalizedStatus(id: Long, code: String, message: String) {
  def this(n: NormalizedStatusDB) = this(n.id, n.code, n.message)
}

object NormalizedStatus {
  val PaidCode = "2"
  val PartiallyPaidCode = "5"
}

case class BankResponseStatus(id: Long, code: String, message: String, internalError: Boolean, bank: Bank, normalizedStatus: NormalizedStatus) {
  def this(b: BankResponseStatusDB, bank: Bank, normalizedStatus: NormalizedStatus) = this(b.id, b.code, b.message, b.internalError, bank, normalizedStatus)
}

case class CascadeLog(rule: String, items: Seq[CascadeLogItem]) {
  def this(c: CascadeLogDB, items: Seq[CascadeLogItem]) = this(c.ruleCode, items)
}

case class CascadeLogItem(bank: String, agreementCode: String, responseCode: Option[String], responseMessage: Option[String], exception: Option[String], creation: LocalDateTime) {
  def this(c: CascadeLogItemDB) = this(c.codeBank, c.agreementCode, c.responseCode, c.responseMessage, c.exception, c.creation)
}

case class Fine(percentage: Option[Int], interestPercentage: Option[Int], startDate: LocalDate)

case class Discount(amount: Int, deadline: LocalDate)

case class Payer(documentType: String,
                 documentNumber: String,
                 name: String,
                 street: String,
                 district: String,
                 city: String,
                 state: String,
                 postalCode: String) {
  def this(p: PayerDB) = this(p.documentType, p.documentNumber, p.name, p.street.get, p.district.get, p.city.get, p.state.get, p.postalCode.get)
}

case class Recipient(name: String, agreementCode: String, agencyCode: String, documentNumber: String) {
  def this(r: RecipientDB) = this(r.name.get, r.agreementCode.get, r.agencyCode.get, r.documentNumber.get)
}

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
                  recipient: Option[Recipient]) {
  def this(b: BoletoDB) = this(b.barcode, b.paymentCode, b.bankNumber, b.gatewayNumber, b.accept, b.boletoType, b.chargeType, b.amount,
    b.amountReduction, Option.empty[Fine], Option.empty[Discount], b.issueDate, b.registerDate, b.dueDate, b.startProtest, b.expirationDate, b.message,
    new Payer(b.payer), Option(new Recipient(b.recipient)))
}

case class Transaction(id: Long,
                       referenceCode: String,
                       establishment: Establishment,
                       var status: String, // TODO ENUM
                       var paidAmount: Option[Int],
                       var paymentDate: Option[LocalDate],
                       boleto: Boleto,
                       bankAgreement: Option[BankAgreement],
                       nsu: String,
                       nsuDate: LocalDate,
                       var normalizedStatus: Option[NormalizedStatus],
                       cascadeLog: Option[CascadeLog],
                       bankResponseCode: Option[String],
                       bankResponseStatus: Option[String],
                       notificationUrl: String,
                       payments: Seq[Payment],
                       creation: LocalDateTime,
                       updated: LocalDateTime,
                       deleted: Boolean) {
  def this(t: TransactionDB, establishment: Establishment, boleto: Boleto, bankAgreement: Option[BankAgreement], normalizedStatus: Option[NormalizedStatus], cascadeLog: Option[CascadeLog], payments: Seq[Payment]) = {
    this(t.id, t.referenceCode, establishment, t.status, t.paidAmount, t.paymentDate, boleto, bankAgreement, t.nsu, t.nsuDate, normalizedStatus, cascadeLog,
      t.bankResponseCode, t.bankResponseStatus, t.notificationUrl, payments, t.creation, t.updated, t.deleted)
  }
}

case class Payment(id: Long,
                   amount: Int,
                   creator: String,
                   nsa: String,
                   nsr: String,
                   paymentDate: LocalDate,
                   creditDate: LocalDate,
                   notificationId: Option[String],
                   creation: LocalDate) {
  def this(p: PaymentDB) = this(p.id, p.amount, p.creator, p.nsa, p.nsr, p.paymentDate, p.creation, p.notificationId, p.creation)
}

case class Configuration(key: String, values: Seq[String]) {
  def this(configs: Seq[ConfigurationDB]) = this(configs.head.key, configs.map(_.value))
}
