package services

import domain.{Configuration, Tables}
import javax.inject.Singleton
import services.RepositoryUtils.db
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.{ExecutionContext, Future}

sealed trait ConfigurationRepository {
  def findByKey(key: Option[String])(implicit executionContext: ExecutionContext): Future[Seq[Configuration]]

  def updateConfiguration(newConfiguration: Configuration): Future[Int]
}

@Singleton
class ConfigurationRepositoryImpl extends ConfigurationRepository {

  override def findByKey(key: Option[String])(implicit executionContext: ExecutionContext): Future[Seq[Configuration]] = {
    val dbioAction = key.map(k => Tables.configurations.filter(c => c.key like s"%$k%"))
      .getOrElse(Tables.configurations)
      .sortBy(_.key)
      .sortBy(_.index.asc)
      .result

    db.run(dbioAction)
      .map {
        _.groupBy(_.key).values
          .map(new Configuration(_))
          .toSeq
      }
  }

  override def updateConfiguration(newConfiguration: Configuration): Future[Int] = ???

  /*{
     val updateConfiguration = Tables.configurations.filter(_.id === newConfiguration.id)
       .map(config => (config.key, config.value))
       .update((newConfiguration.key, Option(newConfiguration.value)))

     db.run(updateConfiguration)
   }*/
}