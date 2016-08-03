package controllers

import javax.inject._
import play.api._
import play.api.mvc._

/**
  * Created by matiasimc on 03-08-16.
  */

@Singleton
class SerieController @Inject() extends Controller {
  def show(id: Long) = Action {
    /* TODO this should search a Serie on the DB by an ID */
    Ok(views.html.serie("Game of Thrones"))
  }
}