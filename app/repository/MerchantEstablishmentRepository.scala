package repository

import domain._
import javax.inject.Singleton
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.{ExecutionContext, Future}

sealed trait MerchantEstablishmentRepository {
  def findByEstablishment(establishment: Establishment)(implicit ec: ExecutionContext): Future[Seq[MerchantEstablishment]]
  def findByMerchant(merchant: Merchant)(implicit ec: ExecutionContext): Future[Seq[MerchantEstablishment]]
}

@Singleton
class MerchantEstablishmentRepositoryImpl extends MerchantEstablishmentRepository {
  override def findByEstablishment(establishment: Establishment)(implicit ec: ExecutionContext): Future[Seq[MerchantEstablishment]] = {
    val merchantEstablishmentFilter = Tables.merchantEstablishments.filter(_.establishmentId === establishment.id)

    val dbioAction = for {
      (me, m) <- merchantEstablishmentFilter join Tables.merchants on (_.merchantId === _.id)
    } yield (me, m)

    RepositoryUtils.db.run(dbioAction.result)
      .map(_.map(result => MerchantEstablishment(result._1.id, new Merchant(result._2), establishment)))
  }

  override def findByMerchant(merchant: Merchant)(implicit ec: ExecutionContext): Future[Seq[MerchantEstablishment]] = {
    val merchantEstablishmentFilter = Tables.merchantEstablishments.filter(_.merchantId === merchant.id)

    val dbioAction = for {
      (me, e) <- merchantEstablishmentFilter join Tables.establishments on (_.establishmentId === _.id)
    } yield (me, e)

    RepositoryUtils.db.run(dbioAction.result)
      .map(_.map(result => MerchantEstablishment(result._1.id, merchant, new Establishment(result._2))))
  }
}
