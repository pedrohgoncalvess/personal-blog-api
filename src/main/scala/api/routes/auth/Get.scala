package api.routes.auth

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives
import api.utils.Authentication


class Get extends Directives {

  val auth = new Authentication

  val route =
    path("auth") {
      authenticateBasic(realm = "secure site", auth.myUserPassAuthenticator) { user =>
        authorize(user.admin) {
          get {
            complete(StatusCodes.OK)
        }
        }
      }
    }

}
