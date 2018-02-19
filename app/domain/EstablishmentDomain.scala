package domain

import slick.lifted.Tag
import slick.jdbc.MySQLProfile.api._

object EstablishmentDomain {

  case class Establishment(id : Option[Long], name : String, code : String)

  class EstablishmentTable(tag: Tag) extends Table[Establishment](tag, "establishment") {
    def id = column[Option[Long]]("idt_establishment", O.PrimaryKey, O.AutoInc)
    def name = column[String]("nam_establishment")
    def code = column[String]("cod_establishment")

    def * = (id, name, code) <> ((Establishment.apply _).tupled, Establishment.unapply)
  }

  val establishments = TableQuery[EstablishmentTable]
}
