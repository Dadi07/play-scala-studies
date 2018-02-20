package domain

import slick.lifted.Tag

import slick.jdbc.MySQLProfile.api._

object NormalizedStatusDomain {

  case class NormalizedStatusDB(id : Long, code : String, message : String)

  class NormalizedStatusTable(tag: Tag) extends Table[NormalizedStatusDB](tag, "normalized_status") {
    def id = column[Long]("idt_normalized_status", O.PrimaryKey, O.AutoInc)
    def code = column[String]("cod_status", O.Unique)
    def messsage = column[String]("des_status")

    def * = (id, code, messsage) <> ((NormalizedStatusDB.apply _).tupled, NormalizedStatusDB.unapply)
  }

  val normalizedStatus = TableQuery[NormalizedStatusTable]
}
