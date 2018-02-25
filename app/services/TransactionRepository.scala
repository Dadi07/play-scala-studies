package services

import javax.inject.Singleton

import domain.BoletoGatewayDomain.{Establishment, Transaction}
import domain.BoletoTransactionDomain._
import domain.CascadeLogDomain.{cascadeLogItems, cascadeLogs}
import domain.{BoletoTransactionDomain, CascadeLogDomain, EstablishmentDomain, NormalizedStatusDomain}
import domain.EstablishmentDomain._
import domain.NormalizedStatusDomain.normalizedStatus
import play.api.Logger
import services.RepositoryUtils.db
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.Future

sealed trait TransactionRepository {

  def findTransactionsByReference(referenceCode: String): Future[Transaction]
}

@Singleton
class TransactionRepositoryImpl extends TransactionRepository {
  override def findTransactionsByReference(referenceCode: String): Future[Transaction] = {
    val transactionData = for {
      t <- transactions if t.referenceCode === referenceCode
      e <- establishments if t.establishmentId === e.id
      b <- boletos if t.boletoId === b.id
      n <- normalizedStatus if t.normalizedStatusId === n.id
      c <- cascadeLogs if t.cascadeLogId === c.id
      ci <- cascadeLogItems if ci.cascadeLogId === c.id
      p <- payments if p.transactionId === t.id
    } yield (t, e, b, n, c, ci, p)

    db.run(transactionData.result)
      .map { seq =>
        val tuple = seq.head
        val e = tuple._2
        val establishment = new Establishment(e)

        val n = tuple._4
      }
  }
}
