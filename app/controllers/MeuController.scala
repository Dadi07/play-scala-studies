package controllers

import javax.inject.Inject

import play.api.mvc.{AbstractController, ControllerComponents}

class MeuController @Inject()(cc : ControllerComponents) extends AbstractController(cc) {

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
}
