package controllers

/**
  * Created by nacho on 18-08-16.
  */

import javax.inject._

import models.daos.{EpisodeDAO, SeasonDAO, SeriesDAO}
import models.entities.{Series, SearchedSeries}
import Commands.GET
import actors.{Update, UpdaterActor}
import akka.actor.{Actor, ActorRef, ActorSystem, Inbox, Props}
import akka.util.Timeout
import play.api.mvc._
import trakt.Trakt
import play.api.libs.json._
import akka.pattern.ask
import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}

@Singleton
class ListController @Inject()(seriesDAO: SeriesDAO, seasonDAO: SeasonDAO, episodeDAO: EpisodeDAO)(implicit ec: ExecutionContext, system: ActorSystem) extends Controller {
  //val updaterActor = system.actorOf(UpdaterActor.props(seriesDAO, seasonDAO, episodeDAO), "UpdaterList")
  //implicit val timeout = Timeout(2 minutes)
  def show() = Action {
    implicit request => {
      GET(request, "serieName") match {
        case Some(serieName) => {
            val series = Trakt.seqShow(serieName).filter((x: SearchedSeries) => {Await.result(seriesDAO.findByIdTrakt(x.traktId),Duration.Inf) match {
              case Some(x) => false
              case None => true
            }
          })
          Ok(views.html.search(series, serieName))
      }
        case None => Ok(views.html.search(List(), "not Valid"))
      }

    }
  }

  def add(id: Long) = Action.async { implicit request => {
    val json = Json.parse(Trakt.show_info(id))
    val title = (json \ "title").validate[String].getOrElse("Not Found")
    val description = (json \ "overview").validate[String].getOrElse("Not Found")
    val seasonsNumber = 0
    val status = (json \ "status").validate[String].getOrElse("Not Found")
    val requested = false
    val images = Json.parse(Trakt.show_images(id))
    val image = (images \ "images" \ "fanart" \ "thumb").validate[String].getOrElse(routes.Assets.versioned("images/cebolla-echala-a-la-olla.png").toString)
    seriesDAO.findByIdTrakt(id).map { element =>
      element match {
        case Some(serie) => (Ok("Already in library"))
        case None => {
          seriesDAO.insert(Series(0, title, description, seasonsNumber, status, image, requested, id))
    //      val resp = updaterActor ? Update
    //      val msg = Await.result(resp, timeout.duration).asInstanceOf[String]
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
