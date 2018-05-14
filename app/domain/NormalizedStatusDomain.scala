package domain

import slick.lifted.Tag

import slick.jdbc.MySQLProfile.api._

case class NormalizedStatusDB(id: Long, code: String, message: String)

case class BankResponseStatusDB(id: Long, code: String, message: String, internalError: Boolean, bankId: Long, normalizedStatusId: Long)

class NormalizedStatusTable(tag: Tag) extends Table[NormalizedStatusDB](tag, "normalized_status") {
  def id = column[Long]("idt_normalized_status", O.PrimaryKey, O.AutoInc)

  def code = column[String]("cod_status", O.Unique)

  def messsage = column[String]("des_status")

  def * = (id, code, messsage) <> ((NormalizedStatusDB.apply _).tupled, NormalizedStatusDB.unapply)
}

class BankResponseStatusTable(tag: Tag) extends Table[BankResponseStatusDB](tag, "bank_response_status") {
  def id = column[Long]("idt_bank_response_status", O.PrimaryKey, O.AutoInc)

  def code = column[String]("cod_response")

  def message = column[String]("des_response")

  def internalError = column[Boolean]("flg_internal_error")

  def bankId = column[Long]("idt_bank")

  def normalizedStatusId = column[Long]("idt_normalized_status")

  def * = (id, code, message, internalError, bankId, normalizedStatusId) <> ((BankResponseStatusDB.apply _).tupled, BankResponseStatusDB.unapply)
}
