package actors

import akka.actor.{Actor, ActorSystem, Props}
import akka.actor.Actor.Receive
import models.daos.{EpisodeDAO, SeasonDAO, SeriesDAO}
import javax.inject.{Inject, Singleton}

import controllers.routes
import models.entities.{Season, Series}
import play.api.libs.json.{JsArray, JsValue, Json}
import trakt.Trakt

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

/**
  * Created by sking32 on 19/08/16.
  */


object UpdateSerieActor {
  def props(serie: Series, seriesDAO: SeriesDAO, seasonDAO: SeasonDAO, episodeDAO: EpisodeDAO)(implicit system: ActorSystem, ec: ExecutionContext) =
    Props(new UpdateSerieActor(serie, seriesDAO, seasonDAO, episodeDAO)(system, ec))
}


class UpdateSerieActor @Inject() (serie: Series,seriesDAO: SeriesDAO, seasonDAO: SeasonDAO, episodeDAO: EpisodeDAO)
                             (implicit system: ActorSystem, ec: ExecutionContext)extends Actor{
  def receive: Receive = {
    case Update => {
      println("Actor for " + serie.title)

      val preSeasons: Seq[Season] = Await.result(seasonDAO.seasonsOfId(serie.id), Duration.Inf)
      val querySeries :JsValue = Json.parse(Trakt.get_seasons(serie.idTrakt))
      querySeries match {
        case JsArray(elements) => {
          //println("Number of seasons for " + serie.title + "=" + elements.length) , actualizar numero de seasons, mismo recursivo
          elements.map( jsSeason => {
            val traktId: Long = (jsSeason \ "ids" \ "trakt").validate[Long].get
            preSeasons.filter(season => season.idTrakt == traktId) match {
              case Seq() => {
                println("Agregar " + traktId + " for " + serie.title)
                val seasonNumber: Int = (jsSeason \ "number").validate[Int].get
                val a : Future[Long] = seasonDAO.insert(Season(0, serie.id, seasonNumber, "Season "+ seasonNumber, "", 0, "" ,(jsSeason \ "images" \ "thumb" \ "full").validate[String].getOrElse(routes.Assets.versioned("images/cebolla-echala-a-la-olla.png").toString), false, traktId))//Dummy description, state, epnumber
                Await.result(a, Duration.Inf)
              }
              case p => println("Ya se tiene season con traktId "+ traktId)
            }
          }
            )
          //Update new number of seasons
          seriesDAO.update(Series(serie.id, serie.title, serie.description, elements.length, serie.state, serie.image, serie.requested, serie.idTrakt))
      }
      }
      val seasons: Seq[Season] = Await.result(seasonDAO.seasonsOfId(serie.id), Duration.Inf)

      sender ! self.path.toString

      val seasonActor =  seasons.map(
        season => {
          system.actorOf(UpdateSeasonActor.props(serie, season, seasonDAO, episodeDAO), "Updater-season-"+season.id)
        }
      )
      for (actor <- seasonActor) actor ! Update //async call



    }

  }
}
