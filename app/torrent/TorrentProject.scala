package torrent

import java.io._
import sys.process._
import scalaj.http.{HttpResponse, Http}
import java.net._
import play.api.libs.json._
/**
  * Created by matiasimc on 03-08-16.
  */
object TorrentProject {
  /*
  e.g. showName="game of thrones", episodeCode="S06E03", videoQuality="720p"
  TODO fix shows with similar names like "Lost" and "Lost Girl"
   */
  val API_URL = "https://torrentproject.se/?out=json&num=50&s="
  def get_torrent_hash(showName: String, episodeCode: String, videoQuality: String) : Option[String] = {
    val request = Http(API_URL+URLEncoder.encode(showName, "UTF-8")+" "+episodeCode+" "+videoQuality)
      .header("Content-Type", "application/json").asString
    val json = Json.parse(request.body)
    val results = Math.min((json \ "total_found").validate[String].get.toInt, 50)
    for (i <- 1 to results ) {
      val title = (json \ (""+i) \ "title").get.as[String]
      if ((title contains episodeCode) || (title contains episodeCode.toLowerCase))
        return Some((json \ (""+i) \ "torrent_hash").get.toString)
    }
    None
  }

  /**
    * Copy all bytes from input to output. Don't close any stream.
    */
  def copy(in: InputStream, out: OutputStream) : Unit = {
    val buf = new Array[Byte](1 << 20) // 1 MB
    while (true)
    {
      val read = in.read(buf)
      if (read == -1)
      {
        out.flush
        return
      }
      out.write(buf, 0, read)
    }
  }

  def download_torrent(torrentHash: Option[String]) = {
    torrentHash match {
      case Some(x) => {
        val connection = new URL("http://itorrents.org/torrent/"+x+".torrent").openConnection
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11")
        val in = connection.getInputStream
        try {
          val folder = new File("torrents/")
          folder.mkdir()
          val outFile = new File("torrents/"+x+".torrent")
          val out = new FileOutputStream(outFile)
          try {
            copy(in, out)
          }
          finally out.close
        }
        finally in.close
      }
      case None => new Exception("Torrent not found in torrentproject")
    }
  }

}
