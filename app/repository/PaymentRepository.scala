package repository

import domain.{Payment, PaymentDB, Tables}
import javax.inject.Singleton
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.Future

sealed trait PaymentRepository {
  def save(payment: Payment, transactionId: Long): Future[Int]
}

@Singleton
class PaymentRepositoryImpl extends PaymentRepository {
  override def save(payment: Payment, transactionId: Long): Future[Int] = {
    val insert = Tables.payments += PaymentDB(id = 1,
      transactionId = transactionId,
      amount = payment.amount,
      creator = payment.creator,
      nsa = payment.nsa,
      nsr = payment.nsr,
      paymentDate = payment.paymentDate,
      creditDate = payment.creditDate,
      notificationId = payment.notificationId,
      creation = payment.creation,
      updated = payment.creation,
      deleted = false)

    RepositoryUtils.db.run(insert)
  }
}