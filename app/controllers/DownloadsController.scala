package controllers

import javax.inject._

import actors.DownloadsActor
import akka.actor.ActorSystem
import play.api.mvc._
import play.api.libs.streams._
import torrent.{KickassAPI, TorrentClient}

import scala.concurrent.ExecutionContext


class DownloadsController @Inject() (implicit ec: ExecutionContext, system: ActorSystem, mat: akka.stream.Materializer) extends Controller {

  def show = Action {
    Ok(views.html.downloads())
  }

  def socket = WebSocket.accept[String, String] { request =>
    ActorFlow.actorRef(out => DownloadsActor.props(out))
  }
}