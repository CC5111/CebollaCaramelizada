package controllers

import javax.inject._

import models.daos.{EpisodeDAO, SeasonDAO, SeriesDAO}
import play.api._
import play.api.mvc._

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext}

@Singleton
class SeasonController @Inject()(seriesDAO: SeriesDAO, seasonDAO: SeasonDAO, episodeDAO: EpisodeDAO)(implicit ec: ExecutionContext) extends Controller  {
  def show(idSeries: Long, idSeason: Long) = Action.async { implicit request =>
    episodeDAO.episodesOfId(idSeason).map { episodes =>
      Await.result(seriesDAO.findById(idSeries), Duration.Inf) match {
        case Some(series) => Await.result(seasonDAO.findById(idSeason), Duration.Inf) match {
          case Some(season) => Ok(views.html.season(series, season, episodes))
          case None => Redirect(routes.SeriesController.show(idSeries))
        }
        case None => Redirect(routes.HomeController.index())
      }
    }
  }
}