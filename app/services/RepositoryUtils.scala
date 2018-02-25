package services

import slick.jdbc.MySQLProfile.api._

object RepositoryUtils {
  lazy val db = Database.forConfig("db")
}
