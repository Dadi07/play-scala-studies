package domain

import slick.lifted.TableQuery

object Tables {
  val banks = TableQuery[BankTable]

  val establishments = TableQuery[EstablishmentTable]

  val normalizedStatus = TableQuery[NormalizedStatusTable]
  val bankResponseStatus = TableQuery[BankResponseStatusTable]

  val configurations = TableQuery[ConfigurationTable]

  val cascadeLogs = TableQuery[CascadeLogTable]
  val cascadeLogItems = TableQuery[CascadeLogItemTable]

  val payments = TableQuery[PaymentTable]
  val boletos = TableQuery[BoletoTable]
  val transactions = TableQuery[TransactionTable]
}
