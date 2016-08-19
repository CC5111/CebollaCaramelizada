package controllers

import javax.inject.{Inject, Singleton}

import models.daos.{EpisodeDAO, SeasonDAO, SeriesDAO}
import play.api.mvc.{Action, Controller}
import actors.{Update, UpdaterActor}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}
import akka.actor.{Actor, ActorRef, ActorSystem, Inbox, Props}
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.duration._

/**
  * Created by sking32 on 19/08/16.
  */
@Singleton
class ActorController @Inject()(seriesDAO: SeriesDAO, seasonDAO: SeasonDAO, episodeDAO: EpisodeDAO)(implicit ec: ExecutionContext, system: ActorSystem) extends Controller  {
  val updaterActor = system.actorOf(UpdaterActor.props(seriesDAO, seasonDAO, episodeDAO), "Updater")
  implicit val timeout = Timeout(2 minutes)

  def updateSeries() = Action.async { implicit request =>
    val resp = updaterActor ?   Update
    val msg = Await.result(resp, timeout.duration).asInstanceOf[String]
    Future(Ok(views.html.actor(msg)))
  }

}
