package torrent

import java.io.{FileInputStream, BufferedInputStream, File}

import org.bitlet.wetorrent.{Torrent, Metafile}
import org.bitlet.wetorrent.disk.PlainFileSystemTorrentDisk
import org.bitlet.wetorrent.peer.IncomingPeerListener

/**
  * Created by matiasimc on 06-08-16.
  */
object TorrentClient {
  val peerListener = new IncomingPeerListener(6881)
  peerListener.start()
  var torrentMap = Map[Long, Torrent]()
  /*
  Add torrent from an existent torrent file with name <hash>.torrent in folder torrents, and starts the download
  to downloadPath
   */
  def add_torrent(torrentFile : File, trakt_id: Long, downloadPath: String) = {
    val dPath = new File(downloadPath)
    dPath.mkdir()
    val metaFile = new Metafile(new BufferedInputStream(new FileInputStream(torrentFile)))
    val tdisk = new PlainFileSystemTorrentDisk(metaFile, dPath)
    tdisk.init()
    val t = new Torrent(metaFile, tdisk, peerListener)
    torrentMap = torrentMap + (trakt_id -> t)
    t.startDownload()
    new Thread(new Runnable {
      def run(): Unit = {
        while (!t.isCompleted) {
          Thread.sleep(5000)
          t.tick()
        }
      }
    }).start()
  }
  def get_peers(trakt_id: Long) = {
    torrentMap(trakt_id).getPeersManager.getActivePeersNumber
  }
  def get_percentage(trakt_id: Long) = {
    1f*torrentMap(trakt_id).getPeersManager.getDownloaded/torrentMap(trakt_id).getMetafile.getLength * 100
  }
  def remove(trakt_id: Long) = {
    torrentMap(trakt_id).stopDownload()
    torrentMap = torrentMap - trakt_id
  }

}
