package torrent

import java.net.{URL, URLEncoder}
import java.io.{File, FileOutputStream, InputStream, OutputStream}

import net.ruippeixotog.scalascraper.browser.JsoupBrowser
/**
  * Created by matias on 19-08-16.
  */
object KickassAPI {
  val browser = JsoupBrowser()
  val kat_url = "http://kat.al/search.php?q="
  def download_torrent_file(showName : String, season: Int, episode: Int) = {
    val seasonString = ("S"+(season/10)).concat(""+(season % 10))
    val episodeString = ("E"+(episode/10)).concat(""+(episode % 10))
    val results = browser.get(kat_url+URLEncoder.encode(showName, "UTF-8")+"%20"+seasonString+episodeString)
    var download_url = results.body.select("i.ka.ka16.ka-arrow-down").head.parent.get.attrs("href")
    download_url = download_url.substring(0,download_url.indexOf("?title="))
    val download_file = new File("torrents/"+showName.replaceAll(" ", ".")+"."+seasonString+episodeString+".torrent")
    val connection = new URL(download_url).openConnection
    connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11")
    val in = connection.getInputStream
    try {
      val folder = new File("torrents/")
      folder.mkdir()
      val outFile = download_file
      val out = new FileOutputStream(outFile)
      try {
        copy(in, out)
      }
      finally out.close
    }
    finally in.close
    download_file
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

}
