package controllers

/**
  * Created by nacho on 18-08-16.
  */

import javax.inject._
import models.daos.{SeasonDAO, SeriesDAO}
import Commands.{GET}
import play.api.mvc._
import trakt.Trakt
import play.api.libs.json._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ListController @Inject()(seriesDAO: SeriesDAO, seasonDAO: SeasonDAO)(implicit ec: ExecutionContext) extends Controller {
  def show() = Action {
    implicit request => {
      GET(request,"serieName") match {
        case Some(serieName) => Ok(views.html.search(Trakt.seqShow(serieName), serieName))
        case None => Ok(views.html.search(List(), "not Valid"))
      }

    }
  }

  def add(id: Long) =  Action {
    Ok("")
  }
  def json(id: Long) = Action {
    Ok(Json.prettyPrint(Json.parse(Trakt.getAllEpisodes(id))))
  }
}
