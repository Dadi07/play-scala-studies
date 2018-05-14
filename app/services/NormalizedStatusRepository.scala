package services

import domain._
import javax.inject.{Inject, Singleton}
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.{ExecutionContext, Future}

sealed trait NormalizedStatusRepository {
  def findByCode(code: Option[String])(implicit ec: ExecutionContext): Future[Seq[NormalizedStatus]]

  def findById(id: Long)(implicit ec: ExecutionContext): Future[Option[NormalizedStatusFullData]]
}

sealed trait BankResponseStatusRepository {
  def findByFilter(code: Option[String], internalError: Option[Boolean], bankId: Option[Long], normalizedStatusId: Option[Long])(implicit ec: ExecutionContext): Future[Seq[BankResponseStatus]]
}

case class NormalizedStatusFullData(normalizedStatus: NormalizedStatus, bankResponses: Seq[BankResponseStatus])

@Singleton
class NormalizedStatusRepositoryImpl @Inject()(bankResponseStatusRepository: BankResponseStatusRepository) extends NormalizedStatusRepository {
  override def findByCode(code: Option[String])(implicit ec: ExecutionContext): Future[Seq[NormalizedStatus]] = {
    val dbAction = code.map(c => Tables.normalizedStatus.filter(_.code === c))
      .getOrElse(Tables.normalizedStatus)
      .result

    RepositoryUtils.db.run(dbAction)
      .map(_.map(new NormalizedStatus(_)))
  }

  override def findById(id: Long)(implicit ec: ExecutionContext): Future[Option[NormalizedStatusFullData]] = {
    val dBIOAction = Tables.normalizedStatus.filter(_.id === id).result

    RepositoryUtils.db.run(dBIOAction)
      .map(mapRow)
      .flatMap {
        case None => Future(Option.empty)
        case Some(normalizedStatus) => bankResponseStatusRepository.findByFilter(Option.empty, Option.empty, Option.empty, Option(normalizedStatus.id))
          .map {
            case Nil => Option.empty
            case responses => Option(NormalizedStatusFullData(normalizedStatus, responses))
          }
      }
  }

  private def mapRow(seq: Seq[NormalizedStatusDB]): Option[NormalizedStatus] = {
    seq match {
      case Nil => Option.empty
      case rows => Option(new NormalizedStatus(rows.head))
    }
  }
}

@Singleton
class BankResponseStatusRepositoryImpl extends BankResponseStatusRepository {

  override def findByFilter(code: Option[String], internalError: Option[Boolean], bankId: Option[Long], normalizedStatusId: Option[Long])(implicit ec: ExecutionContext): Future[Seq[BankResponseStatus]] = {
    val banksFilter = bankId.map(id => Tables.banks.filter(_.id === id))
      .getOrElse(Tables.banks)

    val normalizedStatusFilter = normalizedStatusId.map(id => Tables.normalizedStatus.filter(_.id === id))
      .getOrElse(Tables.normalizedStatus)

    val bankResponseStatusFilterByCode = code.map(c => Tables.bankResponseStatus.filter(_.code === c))
      .getOrElse(Tables.bankResponseStatus)

    val bankResponseStatusFilter = internalError.map(i => bankResponseStatusFilterByCode.filter(_.internalError === i))
      .getOrElse(bankResponseStatusFilterByCode)

    val bankResponseStatusQuery = for {
      ((b, n), ba) <- bankResponseStatusFilter join normalizedStatusFilter on (_.normalizedStatusId === _.id) join banksFilter on (_._1.bankId === _.id)
    } yield (b, n, ba)

    RepositoryUtils.db.run(bankResponseStatusQuery.result)
      .map(mapRowToObject)
  }

  private def mapRowToObject(rows: Seq[(BankResponseStatusDB, NormalizedStatusDB, BankDB)]): Seq[BankResponseStatus] = {
    rows.map { r =>
      new BankResponseStatus(r._1, new Bank(r._3),new NormalizedStatus(r._2))
    }
  }
}


