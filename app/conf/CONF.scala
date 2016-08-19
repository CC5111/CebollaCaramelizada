package conf

import java.io.File

/**
  * Created by matias on 19-08-16.
  */

object CONF {
  val media_path = "media"
  val torrent_path = "torrents"

  def build_path(show_name: String, season: Int): String = {
    val path = media_path+File.separator+show_name+File.separator+"season "+season
    new File(path).mkdirs()
    path
  }
}