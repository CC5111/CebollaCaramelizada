package models.entities

case class Series(id: Long, title: String, description: String, seasonsNumber : Int, state: String, image: String, requested: Boolean)  extends BaseEntity

case class Season(id: Long, seriesID: Long, number:Int, title:String, description: String, epsNumber : Int, state: String, image: String, requested: Boolean) extends BaseEntity

case class Episode(id: Long, seasonID: Long, number:Int, title:String, description: String, duration : Int, state: String, image: String, language:String, requested:Boolean, source:String, progress:Double)  extends BaseEntity

case class Subtitle(id: Long, episodeID: Long, language:String, source:String)  extends BaseEntity


