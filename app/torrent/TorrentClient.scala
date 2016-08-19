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
  var torrentMap = Map[TorrentInfo, Torrent]()
  /*
  Add torrent from an existent torrent file with name <hash>.torrent in folder torrents, and starts the download
  to downloadPath
   */
  def add_torrent(torrentHash: String, downloadPath: String) = {
    val torrent = TorrentInfo(torrentHash, downloadPath)
    val dPath = new File(downloadPath)
    dPath.mkdir()
    val tFile = new File(downloadPath+"/" + torrent.torrentHash + ".torrent")
    val metaFile = new Metafile(new BufferedInputStream(new FileInputStream(tFile)))
    val tdisk = new PlainFileSystemTorrentDisk(metaFile, dPath)
    tdisk.init()
    val t = new Torrent(metaFile, tdisk, peerListener)
    torrentMap = torrentMap + (torrent -> t)
    t.startDownload()
    new Thread(new Runnable {
      def run(): Unit = {
        while (!t.isCompleted) {
          Thread.sleep(1000)
          t.tick()
        }
      }
    }).start()
  }
  def get_peers(torrent: TorrentInfo) = {
    torrentMap(torrent).getPeersManager.getActivePeersNumber
  }
  def get_percentage(torrent: TorrentInfo) = {
    1f*torrentMap(torrent).getPeersManager.getDownloaded/torrentMap(torrent).getMetafile.getLength * 100
  }

}
