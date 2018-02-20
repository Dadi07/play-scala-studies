package domain

import java.time.{LocalDate, LocalDateTime}

object BoletoGatewayDTO {

  case class Establishment(id : Long, name : String, code : String)
  case class BoletoTransaction(id : Long, referenceCode : String, establishment : Establishment, status : String, nsuDate: LocalDate, creation: LocalDateTime)

}
