package domain

import slick.lifted.Tag
import slick.jdbc.MySQLProfile.api._

object BoletoTransactionDomain {

  case class BoletoTransaction(id: Option[Long], referenceCode: String, establishment: Long, status : String)

  class BoletoTransactionTable(tag: Tag) extends Table[BoletoTransaction](tag, "boleto_transaction") {
    def id = column[Option[Long]]("idt_transaction", O.PrimaryKey)
    def referenceCode = column[String]("cod_reference")
    def establishmentFK = column[Long]("idt_establishment")
    def status = column[String]("cod_transaction_status")

    def * = (id, referenceCode, establishmentFK, status) <> ((BoletoTransaction.apply _).tupled, BoletoTransaction.unapply)
  }

  val transactions = TableQuery[BoletoTransactionTable]

}

