package controllers

import play.api.mvc._

object Commands{
  def GET(request: Request[AnyContent],name: String) : Option[String] = {
    request.queryString.map { case (k, v) => k -> v.mkString }.get(name)
  }
}
