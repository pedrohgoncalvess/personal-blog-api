package api.routes.auth

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives
import api.utils.Authentication
import database.Operations


class Get extends Directives {

  val dbOperations = new Operations
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
