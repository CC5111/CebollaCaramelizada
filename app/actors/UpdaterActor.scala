package actors

import java.util.concurrent.TimeUnit

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.actor.Actor.Receive
import models.daos.{EpisodeDAO, SeasonDAO, SeriesDAO}
import javax.inject.{Inject, Singleton}

import akka.util.Timeout
import models.entities.Series

import scala.concurrent.duration.{Duration, FiniteDuration}
import scala.concurrent.{Await, ExecutionContext}
import scala.util.{Failure, Success}

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
      // CALL TRAKT HERE (new series?)
      println("Updater Actor")
      val series: Seq[Series] = Await.result(seriesDAO.all, Duration.Inf)
      val seriesActor : Seq[ActorRef] =  Await.result(seriesDAO.all, Duration.Inf).map(
        serie => {
          implicit val timeout = Timeout(FiniteDuration(1, TimeUnit.SECONDS))
          Await.result(context.actorSelection("/user/*/"+"Updater-serie-"+serie.id)
            .resolveOne()
            .recover { case _:Exception =>
              context.actorOf(UpdateSerieActor.props(serie, seriesDAO, seasonDAO, episodeDAO), "Updater-serie-"+serie.id)
            } ,Duration.Inf)
        }
      )
      for (actor <- seriesActor) actor ! Update //async call

      sender ! self.path.toString
    }

  }
}
