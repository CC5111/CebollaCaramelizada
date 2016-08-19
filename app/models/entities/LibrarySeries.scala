package models.entities

case class Series(id: Long, title: String, description: String, seasonsNumber : Int, state: String, image: String, requested: Boolean, idTrakt: Long)  extends BaseEntity

case class Season(id: Long, seriesID: Long, number:Int, title:String, description: String, epsNumber : Int, state: String, image: String, requested: Boolean, idTrakt: Long) extends BaseEntity

case class Episode(id: Long, seasonID: Long, number:Int, title:String, description: String, duration : Int, state: String, image: String, language:String, requested:Boolean, source:String, progress:Double, idTrakt: Long)  extends BaseEntity

case class Subtitle(id: Long, episodeID: Long, language:String, source:String)  extends BaseEntity


/* Useful entities */
case class SearchedSeries(title: String, description: String, image: String, status: String, traktId: Long)
