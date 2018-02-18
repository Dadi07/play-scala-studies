package controllers

import javax.inject.Inject

import domain.BoletoTransactionDomain
import domain.BoletoTransactionDomain.BoletoTransaction
import play.api.Logger
import play.api.libs.json.{JsValue, Json, Writes}
import play.api.mvc.{AbstractController, ControllerComponents}
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class MeuController @Inject()(cc : ControllerComponents, logginAction : LoggingAction) extends AbstractController(cc) {

  implicit val PessoaWrites = new Writes[Pessoa] {
    override def writes(o: Pessoa): JsValue = {
      Json.obj("nome" -> o.name,
        "idade" -> o.age)
    }
  }

  implicit val BoletoTransactionWrites = new Writes[BoletoTransaction] {
    override def writes(t: BoletoTransaction): JsValue = {
      Json.obj("reference_code" -> t.referenceCode,
        "establishment" -> t.establishment,
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

  def transactions = Action.async { implicit  request =>
    val query = BoletoTransactionDomain.transactions.filter(_.referenceCode === "139911000003789")

    val db  =  Database.forConfig("db")

    val result = query.result
    val eventualTypes: Future[Seq[BoletoTransactionDomain.BoletoTransaction]] = db.run(result)

    //eventualTypes.foreach(_.foreach(t => Logger.info(t.status)))
    var jsonTransaction = BoletoTransaction(Option.empty, "123", 2, "teste")

    eventualTypes.map(l => Ok(Json.toJson(l.head)))
    //eventualTypes.onComplete(t => {
    //  Logger.info(t.get.head.toString)
    //  jsonTransaction = t.get.head;
    //})

    //Logger.info(result.statements.head)

    //Ok(Json.toJson(jsonTransaction))
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
