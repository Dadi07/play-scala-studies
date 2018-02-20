package domain

import slick.lifted.Tag
import slick.jdbc.MySQLProfile.api._


object CascadeLogDomain {

  case class CascadeLogDB(id : Long, ruleCode : String)

  case class CascadeLogItemDB(id : Long, cascadeLogId : Long, codeBank : String, responseCode : String, responseMessage : String, exception : String)

  class CascadeLogTable(tag: Tag) extends Table[CascadeLogDB](tag, "cascade_log") {
    def id = column[Long]("idt_cascade_log", O.PrimaryKey, O.AutoInc)
    def ruleCode = column[String]("cod_rule")

    def * = (id, ruleCode) <> ((CascadeLogDB.apply _).tupled, CascadeLogDB.unapply)
  }

  class CascadeLogItemTable(tag: Tag) extends Table[CascadeLogItemDB](tag, "cascade_log_item") {
    def id = column[Long]("idt_cascade_log_item", O.PrimaryKey, O.AutoInc)
    def cascadeLogId = column[Long]("idt_cascade_log")
    def codeBank = column[String]("cod_bank")
    def responseCode = column[String]("cod_response")
    def responseMessage = column[String]("des_response_message")
    def exception = column[String]("des_exception")

    def * = (id, cascadeLogId, codeBank, responseCode, responseMessage, exception) <> ((CascadeLogItemDB.apply _).tupled, CascadeLogItemDB.unapply)

    def cascacadeLog = foreignKey("casclog_cascade_log_item_fk", cascadeLogId, cascadeLogs)(_.id)
  }

  val cascadeLogs = TableQuery[CascadeLogTable]
  val cascadeLogItems = TableQuery[CascadeLogItemTable]
}
