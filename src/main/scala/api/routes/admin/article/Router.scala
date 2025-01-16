package api.routes.admin.article


import akka.http.scaladsl.server.Directives.concat
import akka.http.scaladsl.server.{Directives, Route}

import api.cors.GenericCors


class Router extends GenericCors with Directives:
  private val deletePath = Delete()
  private val getPath = Get()
  private val patchPath = Patch()
  private val postPath = Post()
  
  val router: Route = withCors {
    pathPrefix("admin") {
      concat(
        deletePath.route,
        getPath.route,
        patchPath.route,
        postPath.route
      )
    }
  }
