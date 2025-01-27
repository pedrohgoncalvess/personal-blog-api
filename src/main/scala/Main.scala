import scala.concurrent.ExecutionContextExecutor
import scala.io.StdIn

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.{Directives, Route}
import api.routes.article.Router as ArticleRouter
import api.routes.auth.Router as AuthRouter
import database.migration.Main.flyway


class Routes extends Directives:

  private val articleRouter = new ArticleRouter
  private val authRouter = new AuthRouter

  val route: Route = concat(articleRouter.router, authRouter.router)


object HttpServer extends App:

    println("Executing migrations...")
    flyway.migrate()

    implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "CthaehBlogApi")
    implicit val executionContext: ExecutionContextExecutor = system.executionContext

    private val service = new Routes
    private val port = 9001

    private val bindingFuture = Http().newServerAt("0.0.0.0", port).bind(service.route)

    println(s"Server started at -> http://localhost:$port")
    StdIn.readLine()
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
      