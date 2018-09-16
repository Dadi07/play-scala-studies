package repository

import domain._
import javax.inject.Singleton
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.{ExecutionContext, Future}

sealed trait EstablishmentRepository {
  def findByFilter(establishmentCode: Option[String], merchantCode: Option[String])(implicit ec: ExecutionContext): Future[Seq[Establishment]]
  def findById(id: Long)(implicit ec: ExecutionContext): Future[Option[Establishment]]
}

@Singleton
class EstablishmentRepositoryImpl extends EstablishmentRepository {
  override def findByFilter(establishmentCode: Option[String], merchantCode: Option[String])(implicit ec: ExecutionContext): Future[Seq[Establishment]] = {
    val establishmentFilter = establishmentCode.map(c => Tables.establishments.filter(_.code === c))
        .getOrElse(Tables.establishments)

    val merchantFilter = merchantCode.map(c => Tables.merchants.filter(_.code === c))
      .getOrElse(Tables.merchants)


    val dbAction = for {
      ((e, me), m) <- establishmentFilter join Tables.merchantEstablishments on (_.id === _.establishmentId) join merchantFilter on (_._2.merchantId === _.id)
    } yield e

    RepositoryUtils.db.run(dbAction.result)
      .map(_.distinct.map(new Establishment(_)))
  }

  override def findById(id: Long)(implicit ec: ExecutionContext): Future[Option[Establishment]] = {
    val dbioAction = Tables.establishments.filter(_.id === id).result

    RepositoryUtils.db.run(dbioAction)
      .map(_.headOption.map(new Establishment(_)))
  }
}
