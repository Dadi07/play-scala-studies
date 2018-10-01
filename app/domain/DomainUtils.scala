package domain

import java.sql.{Date, Timestamp}
import java.time.{LocalDate, LocalDateTime}

import slick.ast.BaseTypedType
import slick.jdbc.JdbcType
import slick.jdbc.MySQLProfile.api._

object DomainUtils {
  implicit val localDateConverter: JdbcType[LocalDate] with BaseTypedType[LocalDate] = MappedColumnType.base[LocalDate, Date](
    l => Date.valueOf(l),
    d => d.toLocalDate
  )

  implicit val LocalDateTimeConverter: JdbcType[LocalDateTime] with BaseTypedType[LocalDateTime] = MappedColumnType.base[LocalDateTime, Timestamp](
    l => Timestamp.valueOf(l),
    t => t.toLocalDateTime
  )
}
