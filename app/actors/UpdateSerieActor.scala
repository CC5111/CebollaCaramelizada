package actors

import akka.actor.{Actor, ActorSystem, Props}
import akka.actor.Actor.Receive
import models.daos.{EpisodeDAO, SeasonDAO, SeriesDAO}
import javax.inject.{Inject, Singleton}

import models.entities.{Season, Series}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext}

/**
  * Created by sking32 on 19/08/16.
  */


object UpdateSerieActor {
  def props(idSerie: Long, seasonDAO: SeasonDAO, episodeDAO: EpisodeDAO)(implicit system: ActorSystem, ec: ExecutionContext) =
    Props(new UpdateSerieActor(idSerie, seasonDAO, episodeDAO)(system, ec))
}


class UpdateSerieActor @Inject() (idSerie: Long, seasonDAO: SeasonDAO, episodeDAO: EpisodeDAO)
                             (implicit system: ActorSystem, ec: ExecutionContext)extends Actor{
  def receive: Receive = {
    case Update => {
      // CALL TRAKT HERE
      val seasons: Seq[Season] = Await.result(seasonDAO.seasonsOfId(idSerie), Duration.Inf)
      for (season <- seasons){
        println("Season " + season.title)
      }

      sender ! self.path.toString
    }

  }
}
