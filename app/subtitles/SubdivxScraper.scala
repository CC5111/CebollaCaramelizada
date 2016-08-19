package subtitles

import java.io.{File, FileOutputStream, InputStream, OutputStream}
import java.net.{URL, URLEncoder}

import conf.CONF
import net.ruippeixotog.scalascraper.browser.JsoupBrowser

/**
  * Created by matias on 19-08-16.
  */
object SubdivxScraper {
  val browser = JsoupBrowser()
  val results_url = "http://www.subdivx.com/index.php?accion=5&masdesc=&subtitulos=1&realiza_b=1&buscar="
  def download_subtitle(showName : String, season: Int, episode: Int) = {
    val seasonString = ("S"+(season/10)).concat(""+(season % 10))
    val episodeString = ("E"+(episode/10)).concat(""+(episode % 10))
    val results_page = browser.get(results_url+URLEncoder.encode(showName, "UTF-8")+"%20"+seasonString+episodeString)
    val link = results_page.body.select("#buscador_detalle_sub_datos").head.select("a").last.attrs("href")
    val sub_db = link.charAt(link.lastIndexOf("u")+2)
    val id = link.substring(link.lastIndexOf("id")+3, link.lastIndexOf("&"))
    var extension = ".rar"
    var download_link = "http://www.subdivx.com/sub"+sub_db+"/"+id+extension
    var connection = new URL(download_link).openConnection
    connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11")
    if (connection.getContentLength < 2) {
      extension = ".zip"
      download_link = "http://www.subdivx.com/sub"+sub_db+"/"+id+extension
      connection = new URL(download_link).openConnection
      connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11")
    }
    val in = connection.getInputStream
    try {
      val outFile = new File(CONF.build_path(showName, season)+File.separator+id+extension)
      val out = new FileOutputStream(outFile)
      try {
        copy(in, out)
      }
      finally out.close
    }
    finally in.close
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
