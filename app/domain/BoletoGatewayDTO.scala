package domain

object BoletoGatewayDTO {

  case class EstablishmentTO(id : Option[Long], name : String, code : String)
  case class BoletoTransactionTO(id : Option[Long], referenceCode : String, establishment : EstablishmentTO, status : String)

}
