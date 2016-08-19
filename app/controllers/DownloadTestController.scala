package controllers

import javax.inject.{Inject, Singleton}

import models.daos.{EpisodeDAO, SeasonDAO, SeriesDAO}
import play.api.mvc.{Action, Controller}
import torrent.{KickassAPI, TorrentClient}
import conf._
import subtitles.SubdivxScraper

import scala.concurrent.ExecutionContext

/**
  * Created by matias on 19-08-16.
  */
@Singleton
class DownloadTestController @Inject()(implicit ec: ExecutionContext) extends Controller {
  def show(show_name: String, season: Int, episode: Int) = Action {
    SubdivxScraper.download_subtitle(show_name, season, episode)
    TorrentClient.add_torrent(KickassAPI.download_torrent_file(show_name, season, episode), 0, CONF.build_path(show_name, season))
    Redirect(routes.DownloadsController.show())
  }
}
