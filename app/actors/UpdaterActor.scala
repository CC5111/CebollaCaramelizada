package actors

import akka.actor.{Actor, ActorSystem, Props}
import akka.actor.Actor.Receive
import models.daos.{EpisodeDAO, SeasonDAO, SeriesDAO}
import javax.inject.{Inject, Singleton}

import models.entities.Series

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext}

/**
  * Created by sking32 on 19/08/16.
  */

case object Update

object UpdaterActor {
  def props(seriesDAO: SeriesDAO, seasonDAO: SeasonDAO, episodeDAO: EpisodeDAO)(implicit system: ActorSystem, ec: ExecutionContext) =
    Props(new UpdaterActor(seriesDAO, seasonDAO, episodeDAO)(system, ec))
}


class UpdaterActor @Inject() (seriesDAO: SeriesDAO, seasonDAO: SeasonDAO, episodeDAO: EpisodeDAO)
                             (implicit system: ActorSystem, ec: ExecutionContext)extends Actor{
  def receive: Receive = {
    case Update => {
      // CALL TRAKT HERE
      val series: Seq[Series] = Await.result(seriesDAO.all, Duration.Inf)
      for (serie <- series){
        println("Creting actor for " + serie.title)

      }

      sender ! self.path.toString
    }

  }
}
