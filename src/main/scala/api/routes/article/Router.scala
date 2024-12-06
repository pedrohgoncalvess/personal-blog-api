package api.routes.article

import akka.http.scaladsl.server.Directives.concat
import akka.http.scaladsl.server.Route
import api.cors.GenericCors

class Router extends GenericCors:
  private val getPath = Get()
  
  val router: Route = withCors{
    concat(
      getPath.route
    )
  }

