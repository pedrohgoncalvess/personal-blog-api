package api.routes.article

import akka.http.scaladsl.server.Directives.concat
import akka.http.scaladsl.server.{Directives, Route}
import api.cors.GenericCors
import api.routes.article.patch.Patch
import api.routes.article.get.Get


class Router extends GenericCors with Directives:
  private val deletePath = Delete()
  private val getPath = Get()
  private val patchPath = Patch()
  private val postPath = Post()
  
  val router: Route = withCors {
      concat(
        deletePath.route,
        getPath.tagParameterRoute,
        getPath.articleIdRoute,
        patchPath.route,
        postPath.route
      )
  }
