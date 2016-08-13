package models.persistence

import models.entities._
import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile
import slick.lifted.ForeignKeyQuery

/**
  * The companion object.
  */
object SlickTables extends HasDatabaseConfig[JdbcProfile] {

  protected lazy val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)
  import dbConfig.driver.api._

  abstract class BaseTable[T](tag: Tag, name: String) extends Table[T](tag, name) {
    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
  }

  class SeriesTable(tag: Tag) extends BaseTable[Series](tag, "series") {
    // Titulo de la serie
    def title =  column[String]("TITLE")
    // Descripcion de la serie
    def description = column[String]("DESCRIPTION")
    // Numero de temporadas de la serie
    def seasonsNumber = column[Int]("NUMBER_OF_SEASONS")
    // Estado de la serie
    def state = column[String]("STATE")
    // Path a la imagen de la serie
    def image = column[String]("IMAGE")

    def * = (id, title, description, seasonsNumber, state, image) <> (Series.tupled, Series.unapply)
  }
  val seriesTable : TableQuery[SeriesTable] = TableQuery[SeriesTable]



  class SeasonTable(tag: Tag) extends BaseTable[Season](tag, "season") {
    // Serie a la cual pertenece la temporada
    def seriesID = column[Long]("SERIES_ID")
    // Numero de temporada
    def number = column[Int]("NUMBER")
    // Titulo de la temporada
    def title =  column[String]("TITLE")
    // Descripcion de la temporada
    def description = column[String]("DESCRIPTION")
    // Numero de capitulos de la temporada
    def epsNumber = column[Int]("NUMBER_OF_EPISODES")
    // Estado de la temporada
    def state = column[String]("STATE")
    // Path a la imagen de la temporada
    def image = column[String]("IMAGE")

    def * = (id, seriesID, number, title, description, epsNumber, state, image) <> (Season.tupled, Season.unapply)

    // Foreign Key
    def series: ForeignKeyQuery[SeriesTable, Series] =
      foreignKey("SUP_FK", seriesID, seriesTable)(_.id)
  }
  val seasonTable : TableQuery[SeasonTable] = TableQuery[SeasonTable]



  class EpisodeTable(tag: Tag) extends BaseTable[Episode](tag, "episode") {
    // Temporada a la cual pertenece el capítulo
    def seasonID = column[Long]("SEASON_ID")
    // Numero de capitulo
    def number = column[Int]("NUMBER")
    // Titulo del capitulo
    def title =  column[String]("TITLE")
    // Descripcion del capitulo
    def description = column[String]("DESCRIPTION")
    // Duracion del capitulo (in seconds)
    def duration = column[Int]("DURATION")
    // Estado del capitulo
    def state = column[String]("STATE")
    // Path a la imagen del capitulo
    def image = column[String]("IMAGE")
    // Idioma del capitulo
    def language = column[String]("LANGUAGE")
    // Indica si el capitulo fue solicitado
    def requested = column[Boolean]("REQUESTED")
    // Origen del video (torrent)
    def source = column[String]("SOURCE")
    // Indica el porcentaje de descarga del capitulo
    def progress = column[Double]("PROGRESS")


    def * = (id, seasonID, number, title, description, duration, state, image, language, requested, source, progress) <> (Episode.tupled, Episode.unapply)

    // Foreign Key
    def season: ForeignKeyQuery[SeasonTable, Season] =
      foreignKey("SUP_FK", seasonID, seasonTable)(_.id)
  }
  val episodeTable : TableQuery[EpisodeTable] = TableQuery[EpisodeTable]



  class SubtitleTable(tag: Tag) extends BaseTable[Subtitle](tag, "subtitle") {
    // Capitulo al cual corresponde el subtitulo
    def episodeID = column[Long]("EPISODE_ID")
    // Idioma de los subtitulos
    def language = column[String]("LANGUAGE")
    // Origen del video (torrent)
    def source = column[String]("SOURCE")


    def * = (id, episodeID, language, source) <> (Subtitle.tupled, Subtitle.unapply)

    // Foreign Key
    def episode: ForeignKeyQuery[EpisodeTable, Episode] =
      foreignKey("SUP_FK", episodeID, episodeTable)(_.id)
  }
  val subtitleTable : TableQuery[SubtitleTable] = TableQuery[SubtitleTable]

}

