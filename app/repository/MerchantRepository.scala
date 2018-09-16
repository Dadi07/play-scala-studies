package repository

import domain.{Bank, Merchant, Tables}
import javax.inject.Singleton
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.{ExecutionContext, Future}

sealed trait MerchantRepository {
  def findByCode(code: Option[String])(implicit ec: ExecutionContext): Future[Seq[Merchant]]
}

@Singleton
class MerchantRepositoryImpl extends MerchantRepository {
  override def findByCode(code: Option[String])(implicit ec: ExecutionContext): Future[Seq[Merchant]] = {
    val dbAction = code.map(c => Tables.merchants.filter(_.code === c))
        .getOrElse(Tables.merchants)
        .result

    RepositoryUtils.db.run(dbAction)
      .map(_.map(new Merchant(_)))
  }
}
