import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.{Directives, Route}
import api.routes
import api.routes.Get
import ch.megard.akka.http.cors.scaladsl.CorsDirectives.cors
import database.operations.ArticleQ
import org.json4s.DefaultFormats
import scala.concurrent.ExecutionContextExecutor
import scala.io.StdIn


class Routes extends Directives {

  implicit val formats: DefaultFormats.type = org.json4s.DefaultFormats

  val dbOperations = new ArticleQ

  val articleGetRoute = new routes.article.Get
  val articleDeleteRoute = new routes.article.Delete
  val articlePostRoute = new routes.article.Post
  val articlePutRoute = new routes.article.Put
  val articlesGetRoute = new Get //routes.articles.Get //problem importing, probably caused by routes.article.Get
  val authGetRoute = new routes.auth.Get

  val route: Route = cors() { concat(articleGetRoute.route, articleDeleteRoute.route, articlePostRoute.route, articlePutRoute.route, articlesGetRoute.route, authGetRoute.route) }
}


object HttpServer extends App{

    implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "CthaehBlogApi")

    implicit val executionContext: ExecutionContextExecutor = system.executionContext

    val service = new Routes

    val bindingFuture = Http().newServerAt("0.0.0.0", 8080).bind(service.route)

    println("Server started at -> http://localhost:8080")
    StdIn.readLine()
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
}