package api.cors

import akka.http.scaladsl.model.HttpMethods.*
import akka.http.scaladsl.model.{StatusCode, StatusCodes}
import akka.http.scaladsl.model.headers.*
import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.server.{Directive0, Route}


trait GenericCors {
  
  private val corsResponseHeaders = List(
    `Access-Control-Allow-Origin`.*,
    `Access-Control-Allow-Credentials`(true),
    `Access-Control-Allow-Headers`("Authorization", "Content-Type", "X-Requested-With"),
    `Access-Control-Allow-Methods`(OPTIONS, POST, PUT, GET, DELETE)
  )
  
  protected def corsHandler(route: Route): Route = {
    respondWithHeaders(corsResponseHeaders) {
      route
    }
  }
  
  private def preflight: Route = options {
    complete(StatusCodes.OK)
  }
  
  def withCors(route: Route): Route = {
    corsHandler {
      preflight ~ route
    }
  }
}
