package controllers

import javax.inject.{Inject, Singleton}

import play.api.mvc.{AbstractController, ControllerComponents}

@Singleton
class TransactionController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def searchTransaction(referenceCode: Option[String], bankNumber: Option[String], establishment: Option[String], bank: Option[String]) = Action {

    Ok
  }

}
