package domain

import slick.lifted.Tag
import slick.jdbc.MySQLProfile.api._

case class EstablishmentDB(id: Long, name: String, code: String)

case class EstablishmentBankAgreementDB(id: Long, establishmentId: Long, bankAgreementId: Long)

class EstablishmentTable(tag: Tag) extends Table[EstablishmentDB](tag, "establishment") {
  def id = column[Long]("idt_establishment", O.PrimaryKey, O.AutoInc)

  def name = column[String]("nam_establishment")

  def code = column[String]("cod_establishment")

  def * = (id, name, code) <> ((EstablishmentDB.apply _).tupled, EstablishmentDB.unapply)
}

class EstablishmentBankAgreementTable(tag: Tag) extends Table[EstablishmentBankAgreementDB](tag, "establishment") {
  def id = column[Long]("idt_establishment_bank_agreement", O.PrimaryKey, O.AutoInc)

  def establishmentId = column[Long]("idt_establishment")

  def bankAgreementId = column[Long]("idt_bank_agreement")

  def * = (id, establishmentId, bankAgreementId) <> ((EstablishmentBankAgreementDB.apply _).tupled, EstablishmentBankAgreementDB.unapply)

  def establishment = foreignKey("esta_estbank_fk", establishmentId, Tables.establishments)(_.id)

  def bankAgreement = foreignKey("bankag_estbank_fk", bankAgreementId, Tables.bankAgreements)(_.id)
}

