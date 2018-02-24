package domain

import slick.lifted.Tag
import slick.jdbc.MySQLProfile.api._

object ConfigurationDomain {

  case class ConfigurationDB(id: Long, key: String, value: Option[String])

  case class ConfigurationMultValueDB(id: Long, configurationId: Long, index: Int, value: Option[String])

  class ConfigurationTable(tag: Tag) extends Table[ConfigurationDB](tag, "configuration") {
    def id = column[Long]("idt_config", O.PrimaryKey, O.AutoInc)
    def key = column[String]("des_key")
    def value = column[Option[String]]("des_value")

    def * = (id, key, value) <> ((ConfigurationDB.apply _).tupled, ConfigurationDB.unapply)
  }

  class ConfigurationMultValueTable(tag: Tag) extends Table[ConfigurationMultValueDB](tag, "configuration_mult_value") {
    def id = column[Long]("idt_config_multv", O.PrimaryKey, O.AutoInc)
    def configurationId = column[Long]("idt_config")
    def index = column[Int]("des_index")
    def value = column[Option[String]]("des_value")

    def * = (id, configurationId, index, value) <> ((ConfigurationMultValueDB.apply _).tupled, ConfigurationMultValueDB.unapply)
  }

  val configurations = TableQuery[ConfigurationTable]
  val configurationsMultValue = TableQuery[ConfigurationMultValueTable]

}
