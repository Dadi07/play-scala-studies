package domain

import slick.lifted.Tag
import slick.jdbc.MySQLProfile.api._

case class ConfigurationDB(id: Long, key: String, value: String, index: Int)

class ConfigurationTable(tag: Tag) extends Table[ConfigurationDB](tag, "config") {
  def id = column[Long]("idt_config", O.PrimaryKey, O.AutoInc)

  def key = column[String]("cod_key")

  def value = column[String]("des_value")

  def index = column[Int]("num_index")

  def * = (id, key, value, index) <> ((ConfigurationDB.apply _).tupled, ConfigurationDB.unapply)
}