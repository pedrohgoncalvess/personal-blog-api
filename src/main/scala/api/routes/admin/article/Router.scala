package api.routes.admin.article

import akka.http.scaladsl.server.Directives.concat
import akka.http.scaladsl.server.Route
import api.cors.GenericCors
import api.routes.auth.refresh.Post


class Router extends GenericCors:
  private val deletePath = Delete()
  private val getPath = Get()
  private val patchPath = Patch()
  private val postPath = Post()
  
  val router: Route = withCors {
    concat(
      deletePath.route,
      getPath.route,
      patchPath.route,
      postPath.route
    )
  }
