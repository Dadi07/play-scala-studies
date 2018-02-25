package services

import javax.inject.Singleton

import domain.EstablishmentDomain.{EstablishmentDB, establishments}
import services.RepositoryUtils.db
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.Future

sealed trait EstablishmentRepository {

  def findByCode(code: String): Future[EstablishmentDB]
}

@Singleton
class EstablishmentRepositoryImpl extends EstablishmentRepository {
  override def findByCode(code: String): Future[EstablishmentDB] = {
    val findEstablishment = establishments.filter(_.code === code).result

    db.run(findEstablishment)
      .map(_.head)
  }
}
