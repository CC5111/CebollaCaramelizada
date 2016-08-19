package controllers

/**
  * Created by nacho on 18-08-16.
  */

import javax.inject._
import models.daos.{SeasonDAO, SeriesDAO}
import models.entities.Series
import Commands.{GET}
import play.api.mvc._
import trakt.Trakt
import play.api.libs.json._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ListController @Inject()(seriesDAO: SeriesDAO)(implicit ec: ExecutionContext) extends Controller {
  def show() = Action {
    implicit request => {
      GET(request,"serieName") match {
        case Some(serieName) => Ok(views.html.search(Trakt.seqShow(serieName), serieName))
        case None => Ok(views.html.search(List(), "not Valid"))
      }

    }
  }

  def add(id: Long) =  Action.async { implicit request =>
    {
      val json = Json.parse(Trakt.show_info(id))
      val title = (json \ "title").validate[String].getOrElse("Not Found")
      val description = (json \ "overview").validate[String].getOrElse("Not Found")
      val seasonsNumber = 0
      val status = (json \ "status").validate[String].getOrElse("Not Found")
      val requested = false
      val images = Json.parse(Trakt.show_images(id))
      val image = (images \ "images" \ "fanart" \ "thumb").validate[String].getOrElse(routes.Assets.versioned("images/cebolla-echala-a-la-olla.png").toString)
      seriesDAO.findByTrackId(id).map {element =>
        element match {
          case Some(serie) => (Ok("Already in library"))
          case None => {
            seriesDAO.insert(Series(0, title, description, seasonsNumber, status, image, requested, id))
            (Ok("Added to library"))
          }
        }
      }
    }
  }

  def json(id: Long) = Action {
    Ok(Json.prettyPrint(Json.parse(Trakt.getAllEpisodes(id))))
  }
}
