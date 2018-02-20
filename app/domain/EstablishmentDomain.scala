package domain

import slick.lifted.Tag
import slick.jdbc.MySQLProfile.api._

object EstablishmentDomain {

  case class EstablishmentDB(id : Long, name : String, code : String)

  class EstablishmentTable(tag: Tag) extends Table[EstablishmentDB](tag, "establishment") {
    def id = column[Long]("idt_establishment", O.PrimaryKey, O.AutoInc)
    def name = column[String]("nam_establishment")
    def code = column[String]("cod_establishment")

    def * = (id, name, code) <> ((EstablishmentDB.apply _).tupled, EstablishmentDB.unapply)
  }

  val establishments = TableQuery[EstablishmentTable]
}
