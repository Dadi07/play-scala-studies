package domain

import slick.lifted.Tag
import slick.jdbc.MySQLProfile.api._

case class EstablishmentDB(id: Long, name: String, code: String)

case class EstablishmentBankAgreementDB(id: Long, establishmentId: Long, bankAgreementId: Long)

case class MerchantDB(id: Long, name: String, code: String, password: String)

case class MerchantEstablishmentDB(id: Long, merchantId: Long, establishmentId: Long)

class EstablishmentTable(tag: Tag) extends Table[EstablishmentDB](tag, "establishment") {
  def id = column[Long]("idt_establishment", O.PrimaryKey, O.AutoInc)

  def name = column[String]("nam_establishment")

  def code = column[String]("cod_establishment")

  def * = (id, name, code) <> ((EstablishmentDB.apply _).tupled, EstablishmentDB.unapply)
}

class EstablishmentBankAgreementTable(tag: Tag) extends Table[EstablishmentBankAgreementDB](tag, "establishment_bank_agreement") {
  def id = column[Long]("idt_establishment_bank_agreement", O.PrimaryKey, O.AutoInc)

  def establishmentId = column[Long]("idt_establishment")

  def bankAgreementId = column[Long]("idt_bank_agreement")

  def * = (id, establishmentId, bankAgreementId) <> ((EstablishmentBankAgreementDB.apply _).tupled, EstablishmentBankAgreementDB.unapply)

  def establishment = foreignKey("esta_estbank_fk", establishmentId, Tables.establishments)(_.id)

  def bankAgreement = foreignKey("bankag_estbank_fk", bankAgreementId, Tables.bankAgreements)(_.id)
}

class MerchantTable(tag: Tag) extends Table[MerchantDB](tag, "merchant") {
  def id = column[Long]("idt_merchant", O.PrimaryKey, O.AutoInc)

  def name = column[String]("nam_merchant")

  def code = column[String]("cod_merchant")

  def password = column[String]("cod_password")

  def * = (id, name, code, password) <> ((MerchantDB.apply _).tupled, MerchantDB.unapply)
}

class MerchantEstablishmentTable(tag: Tag) extends Table[MerchantEstablishmentDB](tag, "merchant_establishment") {
  def id = column[Long]("idt_merchant_establishment", O.PrimaryKey, O.AutoInc)

  def merchantId = column[Long]("idt_merchant")

  def establishmentId = column[Long]("idt_establishment")

  def * = (id, merchantId, establishmentId) <> ((MerchantEstablishmentDB.apply _).tupled, MerchantEstablishmentDB.unapply)

  def establishment = foreignKey("esta_mercesta_fk", establishmentId, Tables.establishments)(_.id)

  def merchant = foreignKey("merc_mercesta_fk", merchantId, Tables.merchants)(_.id)
}

