package services

import domain.{Configuration, ConfigurationDB, Tables}
import javax.inject.Singleton
import services.RepositoryUtils.db
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.{ExecutionContext, Future}

sealed trait ConfigurationRepository {
  def getConfigurations(key: Option[String])(implicit executionContext: ExecutionContext): Future[Seq[Configuration]]

  def createConfiguration(configuration: Configuration): Future[Long]

  def updateConfiguration(newConfiguration: Configuration): Future[Int]

  def deleteConfiguration(id: Long): Future[Int]
}

@Singleton
class ConfigurationRepositoryImpl extends ConfigurationRepository {

  override def getConfigurations(key: Option[String])(implicit executionContext: ExecutionContext): Future[Seq[Configuration]] = {
    val dbioAction = Tables.configurations.filter(c =>
      key.map(k => c.key like s"%$k%")
        .getOrElse(LiteralColumn(true))
    ).result

    db.run(dbioAction)
      .map { seq =>
        seq.map { configurationDb =>
          // TODO ver o que fazer no caso default
          Configuration(configurationDb.id, configurationDb.key, configurationDb.value.getOrElse("defaultValue"))
        }
      }
  }

  override def createConfiguration(configuration: Configuration): Future[Long] = {
    val configId = Tables.configurations returning Tables.configurations.map(_.id) += ConfigurationDB(0, configuration.key, Option(configuration.value))

    db.run(configId)
  }

  override def updateConfiguration(newConfiguration: Configuration): Future[Int] = {
    val updateConfiguration = Tables.configurations.filter(_.id === newConfiguration.id)
      .map(config => (config.key, config.value))
      .update((newConfiguration.key, Option(newConfiguration.value)))

    db.run(updateConfiguration)
  }

  override def deleteConfiguration(id: Long): Future[Int] = {
    val deleteConfiguration = Tables.configurations.filter(_.id === id).delete

    db.run(deleteConfiguration)
  }
}