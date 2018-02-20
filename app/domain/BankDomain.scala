package domain

import slick.jdbc.MySQLProfile.api._

object BankDomain {

  case class BankDB(id : Long, code : String, name : String, febrabanCode : String, deleted : Boolean)

  class BankTable(tag: Tag) extends Table[BankDB](tag, "bank") {
    def id = column[Long]("idt_bank", O.PrimaryKey, O.AutoInc)
    def code = column[String]("cod_bank")
    def name = column[String]("nam_bank")
    def febraban = column[String]("cod_febraban_code")
    def deleted = column[Boolean]("flg_deleted")

    def * = (id, code, name, febraban, deleted) <> ((BankDB.apply _).tupled, BankDB.unapply)
  }

  val banks = TableQuery[BankTable]
}
