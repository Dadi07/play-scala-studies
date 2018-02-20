package domain

import java.sql.{Date, Timestamp}
import java.time.{LocalDate, LocalDateTime}

import slick.jdbc.MySQLProfile.api._

object DomainUtils {
  implicit val localDateConverter = MappedColumnType.base[LocalDate, Date](
    l => Date.valueOf(l),
    d => d.toLocalDate
  )

  implicit val localDateTimeConverter = MappedColumnType.base[LocalDateTime, Timestamp](
    l => Timestamp.valueOf(l),
    t => t.toLocalDateTime
  )
}
