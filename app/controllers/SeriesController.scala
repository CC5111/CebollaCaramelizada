package controllers

import javax.inject._

import models.daos.{SeasonDAO,SeriesDAO}
import models.entities.Season
import play.api._
import play.api.mvc._

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext}

/**
  * Created by matiasimc on 03-08-16.
  */

@Singleton
class SeriesController @Inject()(seriesDAO: SeriesDAO, seasonDAO: SeasonDAO)(implicit ec: ExecutionContext) extends Controller  {
  def show(id: Long) = Action.async { implicit request =>
    seasonDAO.seasonsOfId(id).map { seasons =>
      Await.result(seriesDAO.findById(id), Duration.Inf) match {
        case Some(s) => Ok(views.html.series(s, seasons,id))
        case None => Redirect(routes.HomeController.index())
      }

    }
  }
  def uncheck(id:Long, seriesId: Long) = Action {
    Await.result(seasonDAO.uncheckRequested(id),Duration.Inf)
    Redirect(routes.SeriesController.show(seriesId))
  }

  def check(id:Long, seriesId: Long) = Action {
    Await.result(seasonDAO.checkRequested(id),Duration.Inf)
    Redirect(routes.SeriesController.show(seriesId))
  }
}