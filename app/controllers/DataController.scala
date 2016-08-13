package controllers

import javax.inject._
import scala.concurrent.{Future, Await}
import scala.concurrent.duration.Duration

import models.daos.SeriesDAO
import play.api._
import play.api.mvc._

import scala.concurrent.ExecutionContext

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class DataController @Inject() (seriesDAO: SeriesDAO)(implicit ec: ExecutionContext) extends Controller  {

  /**
    * Create an Action to render an HTML page with a welcome message.
    * The configuration in the `routes` file means that this method
    * will be called when the application receives a `GET` request with
    * a path of `/`.
    */
  def index = Action.async { implicit request =>
    Await.result(seriesDAO.createTableWithDummyData, Duration.Inf)
    seriesDAO.all.map { series =>
      Ok(views.html.data("Series", series))
    }
  }

}
