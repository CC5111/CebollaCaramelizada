package controllers

/**
  * Created by nacho on 18-08-16.
  */

import javax.inject._
import models.daos.{SeasonDAO, SeriesDAO}
import Commands.{GET}
import play.api.mvc._
import trakt.Trakt

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ListController @Inject()(seriesDAO: SeriesDAO, seasonDAO: SeasonDAO)(implicit ec: ExecutionContext) extends Controller {
  def show() = Action.async {
    implicit request => {
      GET(request,"serieName") match {
        case Some(name) => Future(Trakt.seqShow(name)).map {
          list => Ok(views.html.search(list, name))
        }
        case None => Future(Ok(views.html.search(List(), "not Valid")))
      }

    }
  }
}
