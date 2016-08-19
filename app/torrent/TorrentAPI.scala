package torrent

import java.io._
import scalaj.http.{HttpResponse, Http}
import java.net._
import play.api.libs.json._
/**
  * Created by matias on 18-08-16.
  */
object TorrentAPI {
  val API_URL = "https://torrentapi.org/pubapi_v2.php"

  def get_token : String = {
    val request = Http(API_URL+"?get_token=get_token")
      .header("Content-Type", "application/json").asString
    val json = Json.parse(request.body)
    (json \ "token").validate[String].get
  }

  def get_torrent_hash(showName: String, season: Int, episode: Int, videoQuality: String) = {
    val seasonString = ("S"+(season/10)).concat(""+(season % 10))
    val episodeString = ("E"+(episode/10)).concat(""+(episode % 10))
    val request = Http(API_URL+"?mode=search&search_string="+URLEncoder.encode(showName,"UTF-8")+" "+seasonString+episodeString+"%20"+videoQuality+"&token="+get_token)
      .header("Content-Type", "application/json").asString
    val json = Json.parse(request.body)
    val magnet_link = ((json \ "torrent_results")(0) \ "download").validate[String].asOpt
    magnet_link match {
      case Some(x) => x.substring(20, x.indexOf("&"))
      case None => new Exception("Torrent not found in torrentproject")
    }
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

  def download_torrent(torrentHash: String) = {
      val connection = new URL("http://itorrents.org/torrent/"+torrentHash+".torrent").openConnection
      connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11")
      val in = connection.getInputStream
      try {
        val folder = new File("torrents/")
        folder.mkdir()
        val outFile = new File("torrents/"+torrentHash+".torrent")
        val out = new FileOutputStream(outFile)
        try {
          copy(in, out)
        }
        finally out.close
      }
      finally in.close
  }
}
