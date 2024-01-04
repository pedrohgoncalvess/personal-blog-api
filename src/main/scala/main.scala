import akka.actor.Actor
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.{Directives, Route}
import api.routes
import database.Operations

import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContextExecutor
import scala.io.StdIn

class Routes extends Directives {

  implicit val formats = org.json4s.DefaultFormats

  val dbOperations = new Operations

  val articleGetRoute = new routes.article.Get
  val articleDeleteRoute = new routes.article.Delete
  val articlePostRoute = new routes.article.Post
  val articlePutRoute = new routes.article.Put

  val route: Route = concat(articleGetRoute.route, articleDeleteRoute.route, articlePostRoute.route, articlePutRoute.route)
}


object HttpServer extends App{

    implicit val system = ActorSystem(Behaviors.empty, "CthaehBlogApi")

    implicit val executionContext: ExecutionContextExecutor = system.executionContext

    val service = new Routes

    val bindingFuture = Http().newServerAt("0.0.0.0", 8080).bind(service.route)

    println("Server started at -> http://localhost:8080")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
}