package controllers

import javax.inject.Inject

import domain.BoletoGatewayDTO.{BoletoTransaction, Establishment}
import domain.{BoletoTransactionDomain, EstablishmentDomain}
import domain.BoletoTransactionDomain.BoletoTransactionDB
import domain.EstablishmentDomain.EstablishmentDB
import play.api.Logger
import play.api.libs.json.{JsValue, Json, Writes}
import play.api.mvc.{AbstractController, ControllerComponents}
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.ExecutionContext.Implicits.global

class MeuController @Inject()(cc : ControllerComponents, logginAction : LoggingAction) extends AbstractController(cc) {

  implicit val PessoaWrites = new Writes[Pessoa] {
    override def writes(o: Pessoa): JsValue = {
      Json.obj("nome" -> o.name,
        "idade" -> o.age)
    }
  }

  implicit val BoletoTransactionWrites = new Writes[BoletoTransactionDB] {
    override def writes(t: BoletoTransactionDB): JsValue = {
      Json.obj("reference_code" -> t.referenceCode,
        "establishment" -> t.establishmentId,
      "status" -> t.status)
    }
  }

  implicit val EstablishmentWrites = new Writes[EstablishmentDB] {
    override def writes(e: EstablishmentDB): JsValue = {
      Json.obj("name" -> e.name,
        "code" -> e.code)
    }
  }

  implicit val BoletoTransactionTOWrites = new Writes[BoletoTransaction] {
    override def writes(t: BoletoTransaction): JsValue = {
      Json.obj("reference_code" -> t.referenceCode,
        "establishment" -> Json.obj("name" -> t.establishment.name,
        "code" -> t.establishment.code),
        "status" -> t.status)
    }
  }

  def helloWorld = Action { request =>
    Ok("Hello " + request + " World")
  }

  def helloWorld2(param : String) = Action { request =>
    Ok("Hello " + param + " "  + request + " World")
  }

  def helloWorld3 = Action { request =>
    //    Ok("Hello " + request + " World")
    Redirect("/teste/TaFunfando")
  }

  def testando = Action { request =>
    if(request.session.isEmpty)
      Created(createBoletoTransactionXML()).as(XML).withSession("testando" -> "sou eu testando")
    else
      Ok(createBoletoTransactionXML()).as(XML).withSession("testando" -> "ja tem")
  }

  def autentica = Action {
    Created("Voce se autenticou").withSession("status" -> "autenticado")
  }

  def testeAutenticacao = Action { request =>
    request.session.get("status")
      .filter(_.equals("autenticado"))
      .map(s => Ok("Deu certo"))
      .getOrElse(Unauthorized("Voce nao tem permissao"))
  }

  def primeiroPost = Action { request =>
    val body = request.body
    val jsonBody = body.asJson

    jsonBody.map(json => Ok((json \ "name").as[String]))
      .getOrElse(BadRequest)
  }

  def postJson = Action(parse.tolerantJson) { request =>
    Ok(request.body)
  }

  def logRequest = logginAction {
    Ok("Recebido")
  }

  def logRequest2 = Logging {
    Action(parse.tolerantJson) { request =>
      Ok("Recebido")
    }
  }

  def contentNegotiation = Action { implicit request =>
    val pessoa = new Pessoa("Douglas", 25)

    request.headers.get("Accept").foreach(Logger.info(_))
    Logger.info(request.acceptedTypes.toString())

    render {
      case Accepts.Json() => Created(Json.toJson(pessoa))
    }
  }

  def transaction = Action.async { implicit  request =>
    val db  =  Database.forConfig("db")
    val query = BoletoTransactionDomain.transactions.filter(_.referenceCode === "139911000003789")
    val result = query.result

    db.run(result).map(l => Ok(Json.toJson(l.head)))

  }

  def establishment = Action.async { implicit  request =>
    val db  =  Database.forConfig("db")
    val query = EstablishmentDomain.establishments.filter(_.code === "bpagnovaiorque1")
    val result = query.result

    db.run(result).map(establishments => Ok(Json.toJson(establishments.head)))
  }

  def searchTransaction(establishment : String, referenceCode : String) = Action.async {
    val db  =  Database.forConfig("db")
    val queryEstablishment = EstablishmentDomain.establishments.filter(_.code === establishment)
    val resultEstablishments = queryEstablishment.result

    val queryTransaction = BoletoTransactionDomain.transactions.filter(_.referenceCode === referenceCode)
    val resultTransactions = queryTransaction.result

    val dbioAction = resultEstablishments zip resultTransactions

    db.run(dbioAction)
      .map{ tuple =>
        val establishmentDb = tuple._1.head
        val transactionDb = tuple._2.head

        val establishmentTO = Establishment(establishmentDb.id, establishmentDb.name, establishmentDb.code)
        val boletoTransaction = BoletoTransaction(transactionDb.id, transactionDb.referenceCode, establishmentTO, transactionDb.status, transactionDb.nsuDate, transactionDb.creation)

        Ok(Json.toJson(boletoTransaction))
      }
  }


  private def createBoletoTransactionXML() = {
    """<BoletoTransactionResponse>
      <transaction>
      <boleto>
      <amount>100</amount>
      <payer>
      <name>João Dionísio</name>
      </payer>
      </boleto>
      </transaction>
      </BoletoTransactionResponse>
    """
  }

  case class Pessoa (name: String, age: Int)
}
