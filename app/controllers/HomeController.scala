package controllers

import javax.inject._

import models.daos.SeriesDAO
import play.api._
import play.api.mvc._

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext}
import models.entities.Series

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject() (seriesDAO: SeriesDAO)(implicit ec: ExecutionContext) extends Controller  {


  /**
   * Create an Action to render an HTML page with a welcome message.
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index = Action.async { implicit request =>
    seriesDAO.all.map { series =>
      Ok(views.html.index(series,
        //Esto deberia ser el discover de series
        Seq(
          Series(0, "The Walking Dead", "The world we knew is gone. An epidemic of apocalyptic proportions has swept the globe causing the dead to rise and feed on the living. In a matter of months society has crumbled. In a world ruled by the dead, we are forced to finally start living. Based on a comic book series of the same name by Robert Kirkman, this AMC project focuses on the world after a zombie apocalypse. The series follows a police officer, Rick Grimes, who wakes up from a coma to find the world ravaged with zombies. Looking for his family, he and a group of survivors attempt to battle against the zombies in order to stay alive.", 7, "In progress", "http://cde.laprensa.e3.pe/ima/0/0/1/2/6/126285.jpg", false, 1),
          Series(0, "Stranger Things", "When a young boy disappears, his mother, a sheriff, and his friends must confront terrifying forces in order to get him back.", 1, "Finished", "https://walter.trakt.us/images/shows/000/104/439/posters/thumb/9e2940add5.jpg", false, 1)
      )))
  }
  }

}
