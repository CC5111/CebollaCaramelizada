package trakt

import scalaj.http.{Http, HttpResponse}
import models.entities.{Series,SearchedSeries}
import java.net.URLEncoder

import controllers.routes
import play.api.libs.json._


/**
  * Created by matiasimc on 03-08-16.
  * Wrapper of Trakt API, methods return JSON strings
  */
object Trakt {
  val API_KEY = "569a84a27212b725c2a99056dc9a4a18560baedf6b3dc56f7bc74457c3c0e65e"
  val API_REFRESH = "da300bba4715294e6844681fa9eb33d5961df988d71b5a31f765b30d85ad9294"
  val CLIENT_ID = "3c7eaee3a6e7066fed2e4d2b1e4e91d6dba01edd38e7f4b73aaf54a955cc9754"
  val API_URL = "https://api.trakt.tv/"

  /* One-use only method, to obtain the api key given an access code */
  def get_key = {
    val request = Http(API_URL + "oauth/token").postData("""{"code":"3afa59b30e0df9982b2a97997390116f23739ea5e37399718a42de5968f89e7a","client_id":"3c7eaee3a6e7066fed2e4d2b1e4e91d6dba01edd38e7f4b73aaf54a955cc9754","client_secret":"90b79cb3afe03e993e58f6962c9e490cc2ad071eb07aae7117312d1abf6c8073","redirect_uri":"http://localhost:9000","grant_type":"authorization_code"}""")
      .header("Content-Type", "application/json")
      .header("Charset", "UTF-8").asString
    request
  }

  /*
 Get results from a given query
  */
  def search_show(query: String): String = {
    val request = Http(API_URL + "search/show?query=" + URLEncoder.encode(query, "UTF-8"))
      .header("Content-Type", "application/json")
      .header("trakt-api-version", "2")
      .header("trakt-api-key", CLIENT_ID).asString
    request.body
  }

  /* Get a sequence of SearchedSeries*/
  def seqShow(query: String): Seq[SearchedSeries] = {
    val array = Json.parse(search_show(query))
    array match {
      case JsArray(elements) => elements.map {
        element => {
          val id = (element \ "show" \ "ids" \ "trakt").validate[Long].get
          val info = Json.parse(show_info(id))
          val images = Json.parse(show_images(id))
          val title = (info \ "title").validate[String].get
          val description = (info \ "overview").validate[String].getOrElse("Not found")
          val status = (info \ "status").validate[String].getOrElse("Not found")
          val image = (images \ "images" \ "fanart" \ "thumb").validate[String].getOrElse(routes.Assets.versioned("images/cebolla-echala-a-la-olla.png").toString)
          SearchedSeries(title, description, image, status, id)
        }
      }
    }
  }

  /* Get a single show given a trakt id, with description and more info */
  def show_info(id: Long): String = {
    val request = Http(API_URL + "shows/" + id + "?extended=full")
      .header("Content-Type", "application/json")
      .header("trakt-api-version", "2")
      .header("trakt-api-key", CLIENT_ID).asString
    request.body
  }

  /* Get a single show given a trakt id, with images urls */
  def show_images(id: Long): String = {
    val request = Http(API_URL + "shows/" + id + "?extended=images")
      .header("Content-Type", "application/json")
      .header("trakt-api-version", "2")
      .header("trakt-api-key", CLIENT_ID).asString
    request.body
  }

  /*
  Get a single season for a show
   */
  def get_season(show_id: Long, season: Int) = {
    val request = Http(API_URL + "shows/" + show_id + "/seasons/" + season + "?extended=images")
      .header("Content-Type", "application/json")
      .header("trakt-api-version", "2")
      .header("trakt-api-key", CLIENT_ID).asString
    request.body
  }

  def getAllEpisodes(show_id: Long) = {
    val request = Http(API_URL + "shows/" + show_id + "/seasons?extended=episodes")
      .header("Content-Type", "application/json")
      .header("trakt-api-version", "2")
      .header("trakt-api-key", CLIENT_ID).asString
    request.body
  }
  /*
  Get the tvdb id for a given trakt
   */

  /*
  Get all seasons from a show, with images urls
   */
  def get_seasons(id: Long): String = {
    val request = Http(API_URL + "shows/" + id + "/seasons?extended=images")
      .header("Content-Type", "application/json")
      .header("trakt-api-version", "2")
      .header("trakt-api-key", CLIENT_ID).asString
    request.body
  }

  /*
  Get a single episode from a show
   */
  def get_episode(show_id: Long, season: Int, episode: Int) = {
    val request = Http(API_URL + "shows/" + show_id + "/seasons/" + season + "/episodes/" + episode + "?extended=images")
      .header("Content-Type", "application/json")
      .header("trakt-api-version", "2")
      .header("trakt-api-key", CLIENT_ID).asString
    request.body
  }


  /*
  Get popular shows
   */
  def get_popular_shows: String = {
    val request = Http(API_URL + "shows/popular")
      .header("Content-Type", "application/json")
      .header("trakt-api-version", "2")
      .header("trakt-api-key", CLIENT_ID).asString
    request.body
  }
}
