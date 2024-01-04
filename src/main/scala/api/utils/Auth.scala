package api.utils

import akka.http.scaladsl.server.directives.Credentials
import api.models.User
import com.password4j.Password
import database.Operations

class Authentication {

  def verifyPassword(hashedPassword:String, rawPassword:String): Boolean = {
    Password.check(rawPassword, hashedPassword).withArgon2()
  }

  def myUserPassAuthenticator(credentials: Credentials): Option[User] = {
    val opr = new Operations
    credentials match {
      case p@Credentials.Provided(id) if p.provideVerify(opr.getUserByUsername(id).password,verifier=verifyPassword) =>
        val user = opr.getUserByUsername(id)
        Some(User(null,null,user.username, user.email, null, user.admin))
      case _ => None
    }
  }
}
