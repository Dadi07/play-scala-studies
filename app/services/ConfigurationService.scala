package services

import javax.inject.Singleton

import domain.BoletoGatewayDomain.Configuration
import domain.ConfigurationDomain.configurations
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.{ExecutionContext, Future}

sealed trait ConfigurationService {
  def getConfiguration(key: String)(implicit executionContext: ExecutionContext): Future[Configuration]

  def getConfigurationValue(key: String): String

  def getConfigurationValues(key: String): List[String]
}

@Singleton
class ConfigurationServiceImpl extends ConfigurationService {
  lazy val db = Database.forConfig("db")

  override def getConfiguration(key: String)(implicit executionContext: ExecutionContext): Future[Configuration] = {
    val dbioAction = configurations.filter(_.key === key).result

    db.run(dbioAction)
      .filter(_.size == 1)
      .map { seq =>
        val configurationDb = seq.head

        Configuration(configurationDb.id, configurationDb.key, configurationDb.value.getOrElse("defaultValue"))
      }
  }

  override def getConfigurationValue(key: String): String = ???

  override def getConfigurationValues(key: String): List[String] = ???
}
