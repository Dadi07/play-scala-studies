package services

import javax.inject.Singleton

import domain.BoletoGatewayDomain.Configuration
import domain.ConfigurationDomain.{ConfigurationDB, configurations}
import services.RepositoryUtils.db
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.{ExecutionContext, Future}

sealed trait ConfigurationService {
  def getConfigurations(key: Option[String])(implicit executionContext: ExecutionContext): Future[Seq[Configuration]]

  def createConfiguration(configuration: Configuration): Future[Long]

  def updateConfiguration(newConfiguration: Configuration): Future[Int]

  def deleteConfiguration(id: Long): Future[Int]
}

@Singleton
class ConfigurationServiceImpl extends ConfigurationService {

  override def getConfigurations(key: Option[String])(implicit executionContext: ExecutionContext): Future[Seq[Configuration]] = {
    val dbioAction = configurations.filter(c =>
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
    val configId = configurations returning configurations.map(_.id) += ConfigurationDB(0, configuration.key, Option(configuration.value))

    db.run(configId)
  }

  override def updateConfiguration(newConfiguration: Configuration): Future[Int] = {
    val updateConfiguration = configurations.filter(_.id === newConfiguration.id)
      .map(config => (config.key, config.value))
      .update((newConfiguration.key, Option(newConfiguration.value)))

    db.run(updateConfiguration)
  }

  override def deleteConfiguration(id: Long): Future[Int] = {
    val deleteConfiguration = configurations.filter(_.id === id).delete

    db.run(deleteConfiguration)
  }
}