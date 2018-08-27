package domain

import slick.jdbc.MySQLProfile.api._

case class BankDB(id: Long, code: String, name: String, febrabanCode: String, deleted: Boolean)

case class BankAgreementDB(id: Long, companyName: String, agreementCode: String, agency: String, documentNumber: String, bankId: Long, deleted: Boolean)

case class DocumentNumberDB(id: Long, documenNumber: String, bankAgreementId: Long)

class BankTable(tag: Tag) extends Table[BankDB](tag, "bank") {
  def id = column[Long]("idt_bank", O.PrimaryKey, O.AutoInc)

  def code = column[String]("cod_bank")

  def name = column[String]("nam_bank")

  def febraban = column[String]("cod_febraban_code")

  def deleted = column[Boolean]("flg_deleted")

  def * = (id, code, name, febraban, deleted) <> ((BankDB.apply _).tupled, BankDB.unapply)
}

class BankAgreementTable(tag: Tag) extends Table[BankAgreementDB](tag, "bank_agreement") {
  def id = column[Long]("idt_bank_agreement", O.PrimaryKey, O.AutoInc)

  def companyName = column[String]("nam_company")

  def agreementCode = column[String]("cod_agreement")

  def agency = column[String]("cod_agency")

  def documentNumber = column[String]("num_document")

  def bankId = column[Long]("idt_bank")

  def deleted = column[Boolean]("flg_deleted")

  def * = (id, companyName, agreementCode, agency, documentNumber, bankId, deleted) <> ((BankAgreementDB.apply _).tupled, BankAgreementDB.unapply)

  def bank = foreignKey("bank_bankagre_fk", bankId, Tables.banks)(_.id)
}

class DocumentNumberTable(tag: Tag) extends Table[DocumentNumberDB](tag, "document_number") {
  def id = column[Long]("idt_document_number", O.PrimaryKey, O.AutoInc)

  def documentNumber = column[String]("cod_document_number")

  def bankAgreementId = column[Long]("idt_bank_agreement")

  def * = (id, documentNumber, bankAgreementId) <> ((DocumentNumberDB.apply _).tupled, DocumentNumberDB.unapply)

  def bankAgreement = foreignKey("bankagree_docnum_fk", bankAgreementId, Tables.bankAgreements)(_.id)
}