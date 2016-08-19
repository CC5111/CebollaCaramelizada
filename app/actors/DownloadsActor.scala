package actors

/**
  * Created by matias on 19-08-16.
  */
import akka.actor._
import play.api.libs.json.{JsArray, JsValue, Json}
import torrent.TorrentClient

object DownloadsActor {
  val client = TorrentClient
  def props(out: ActorRef) = Props(classOf[DownloadsActor], out)
}

class DownloadsActor(out: ActorRef) extends Actor {
  import DownloadsActor._
  def receive = {
    case msg: String => {
      val completed = Json.toJson(for {
        (k,v) <- client.torrentMap
      } yield Map("name"->v.getMetafile.getName,"size"->(""+(v.getMetafile.getLength/1000000)+" MB"),"completed"->client.get_percentage(k).toString))
      out ! (Json.stringify(completed))
    }
  }
}