package api.routes.auth


import akka.http.scaladsl.server.Directives.concat
import akka.http.scaladsl.server.Route

import api.cors.GenericCors
import api.routes.auth.refresh.Post as RefreshPost


class Router extends GenericCors:
  private val postPath = Post()
  private val refreshPostPath = RefreshPost()
  
  val router: Route = withCors {
    concat(
      postPath.route,
      refreshPostPath.route
    )
  }
