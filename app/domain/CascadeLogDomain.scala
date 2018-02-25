package domain

import java.time.LocalDate

import domain.DomainUtils.localDateConverter
import slick.jdbc.MySQLProfile.api._
import slick.lifted.Tag

object CascadeLogDomain {

  case class CascadeLogDB(id: Long, ruleCode: String)

  case class CascadeLogItemDB(id: Long, cascadeLogId: Long, codeBank: String, responseCode: Option[String], responseMessage: Option[String], exception: Option[String], creation: LocalDate)

  class CascadeLogTable(tag: Tag) extends Table[CascadeLogDB](tag, "cascade_log") {
    def id = column[Long]("idt_cascade_log", O.PrimaryKey, O.AutoInc)

    def ruleCode = column[String]("cod_rule")

    def * = (id, ruleCode) <> ((CascadeLogDB.apply _).tupled, CascadeLogDB.unapply)
  }

  class CascadeLogItemTable(tag: Tag) extends Table[CascadeLogItemDB](tag, "cascade_log_item") {
    def id = column[Long]("idt_cascade_log_item", O.PrimaryKey, O.AutoInc)

    def cascadeLogId = column[Long]("idt_cascade_log")

    def codeBank = column[String]("cod_bank")

    def responseCode = column[Option[String]]("cod_response")

    def responseMessage = column[Option[String]]("des_response_message")

    def exception = column[Option[String]]("des_exception")

    def creation = column[LocalDate]("dat_creation")

    def * = (id, cascadeLogId, codeBank, responseCode, responseMessage, exception, creation) <> ((CascadeLogItemDB.apply _).tupled, CascadeLogItemDB.unapply)

    def cascacadeLog = foreignKey("casclog_cascade_log_item_fk", cascadeLogId, cascadeLogs)(_.id)

    def cascadeLog = foreignKey("casclog_cascade_log_item_fk", cascadeLogId, cascadeLogs)
  }

  val cascadeLogs = TableQuery[CascadeLogTable]
  val cascadeLogItems = TableQuery[CascadeLogItemTable]
}
