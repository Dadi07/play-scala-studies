package domain

import slick.lifted.TableQuery

object Tables {
  val banks = TableQuery[BankTable]
  val bankAgreements = TableQuery[BankAgreementTable]
  val documenNumbers = TableQuery[DocumentNumberTable]

  val establishments = TableQuery[EstablishmentTable]
  val establishmentBankAgreements = TableQuery[EstablishmentBankAgreementTable]

  val normalizedStatus = TableQuery[NormalizedStatusTable]
  val bankResponseStatus = TableQuery[BankResponseStatusTable]

  val configurations = TableQuery[ConfigurationTable]

  val cascadeLogs = TableQuery[CascadeLogTable]
  val cascadeLogItems = TableQuery[CascadeLogItemTable]

  val payments = TableQuery[PaymentTable]
  val boletos = TableQuery[BoletoTable]
  val transactions = TableQuery[TransactionTable]
}
