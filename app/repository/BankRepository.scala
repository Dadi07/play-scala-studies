package repository

import domain.{Bank, Tables}
import javax.inject.Singleton
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.{ExecutionContext, Future}

sealed trait BankRepository {
  def findByCode(code: Option[String])(implicit ec: ExecutionContext): Future[Seq[Bank]]
}

@Singleton
class BankRepositoryImpl extends BankRepository {
  override def findByCode(code: Option[String])(implicit ec: ExecutionContext): Future[Seq[Bank]] = {
    val dbAction = code.map(c => Tables.banks.filter(_.code === c))
        .getOrElse(Tables.banks)
        .result

    RepositoryUtils.db.run(dbAction)
      .map(_.map(new Bank(_)))
  }
}
