package actors

import akka.actor.{Actor, ActorSystem, Props}
import akka.actor.Actor.Receive
import models.daos.{EpisodeDAO, SeasonDAO, SeriesDAO}
import javax.inject.{Inject, Singleton}

import controllers.routes
import models.entities.{Episode, Season, Series}
import play.api.libs.json.{JsArray, JsValue, Json}
import trakt.Trakt

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

/**
  * Created by sking32 on 19/08/16.
  */


object UpdateSeasonActor {
  def props(serie: Series, season: Season, seasonDAO: SeasonDAO, episodeDAO: EpisodeDAO)(implicit system: ActorSystem, ec: ExecutionContext) =
    Props(new UpdateSeasonActor(serie, season, seasonDAO, episodeDAO)(system, ec))
}


class UpdateSeasonActor @Inject() (serie: Series, season: Season, seasonDAO: SeasonDAO, episodeDAO: EpisodeDAO)
                                 (implicit system: ActorSystem, ec: ExecutionContext)extends Actor{
  def receive: Receive = {
    case Update => {
      println("Actor for " + season.title)

      val preEpisodes: Seq[Episode] = Await.result(episodeDAO.episodesOfId(season.id), Duration.Inf)
      val queryEpisodes:JsValue = Json.parse(Trakt.get_season(serie.idTrakt, season.number))
      queryEpisodes match {
        case JsArray(elements) => {
          //println("Number of seasons for " + serie.title + "=" + elements.length)
          elements.map( jsEpisode => {
            val traktId: Long = (jsEpisode \ "ids" \ "trakt").validate[Long].get
            preEpisodes.filter(episode => episode.idTrakt == traktId) match {
              case Seq() => {
                println("Agregar " + traktId + " for " + season.title+ " for " + serie.title)
                val episodeNumber: Int = (jsEpisode \ "number").validate[Int].get
                val a : Future[Long] = episodeDAO.insert(Episode(0, season.id, episodeNumber, (jsEpisode \ "title").validate[String].get, "", 0,"", (jsEpisode \ "images" \ "screenshot" \ "full").validate[String].getOrElse(routes.Assets.versioned("images/cebolla-echala-a-la-olla.png").toString), "", false, "", 0, traktId))//Dummy description, epNumber, state, duration, language, source
                Await.result(a, Duration.Inf)
              }
              case p => println("Ya se tiene season con traktId "+ traktId)
            }

          }
          )
          //Update new number of episodes
          seasonDAO.update(Season(season.id, season.seriesID, season.number, season.title, season.description, elements.length, season.state ,season.image, season.requested, season.idTrakt))
        }
      }
      // CALL TRAKT HERE (search for and append new episodes in the season)
      sender ! self.path.toString
    }

  }
}