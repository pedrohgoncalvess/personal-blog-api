package database.operations

import database.CouchConnection
import database.models.{User, UserRepository}


class AuthQ extends CouchConnection {

  def getUserByUsername(username:String): User = {
      couchInstance({ db =>
        val newUserRepo = new UserRepository(db)
        val user = newUserRepo.findByUsername(username)
        user
      })
  }
}

