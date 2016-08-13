package models.entities

case class Series(id: Long, title: String, description: String, seasonNumber : Int, state: String, image: String)

case class Season(id: Long, seriesID: Long, number:Int, title:String, description: String, epsNumber : Int, state: String, image: String)

case class Episode(id: Long, seasonID: Long, number:Int, title:String, description: String, duration : Int, state: String, image: String, language:String, requested:Boolean, source:String, progress:Double)

case class Subtitle(id: Long, episodeID: Long, language:String, source:String)


