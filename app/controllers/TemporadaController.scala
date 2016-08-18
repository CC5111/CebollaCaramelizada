package controllers

import javax.inject._
import play.api._
import play.api.mvc._

@Singleton
class TemporadaController @Inject() extends Controller {
  def show(id: Long) = Action {
    /* TODO this should search a Temporada on the DB by an ID */
    Ok(views.html.temporada("Temporada 1"))
  }
}