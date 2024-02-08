package database

import api.models.{User, UserRepository}
import api.models.{Article, ArticleRepository, CreateArticle, UpdateArticle}
import org.json4s.native.JsonParser
import org.ektorp.{DocumentNotFoundException, ViewQuery}
import org.json4s.DefaultFormats
import java.time.LocalDateTime
import scala.collection.JavaConverters._
import scala.collection.convert.ImplicitConversions.`collection AsScalaIterable`
import scala.concurrent.Future


class Operations extends CouchConnection {

  def jsonStrToMap(jsonStr: String): Map[String, Any] = {

    implicit val formats: DefaultFormats.type = org.json4s.DefaultFormats

    JsonParser.parse(jsonStr).extract[Map[String, Any]]
  }

  import scala.concurrent.ExecutionContext.Implicits.global

  def addNewDocument(newDocument: CreateArticle): Future[Unit] = {
    couchInstance { db =>
      try {
        db.create(newDocument)
        Future.successful()
      } catch {
        case ex: Throwable =>
          println(ex)
          Future.failed(new Exception("An error occurred."))
      }
    }
  }


  def getAllDocuments: Future[Array[Article]] = {
    Future {
      couchInstance { db =>
        val articleRepo = new ArticleRepository(db)
        articleRepo.getAll.asScala.toArray
      }
    }
  }

  def getDocumentsByTag(tag: String): Future[Array[Article]] = {
    Future {
      couchInstance { db =>
        val articleRepo = new ArticleRepository(db)
        val articlesToReturn: java.util.List[Article] = articleRepo.findByTag(tag.toLowerCase())
        articlesToReturn.map(article => article).toArray
      }
    }
  }

  def getDocumentByID(id: String): Future[Article] = {
    Future {
      couchInstance { db =>
        val ArticleRepo = new ArticleRepository(db)
        val document: Article = ArticleRepo.get(id)
        document
      }
    }
  }

  def deleteDocumentByID(id: String, revision: String): Future[Unit] = {
    Future {
      couchInstance({ db =>
        try {
          db.delete(id, revision)
          Future.successful()
        } catch {
          case _: org.ektorp.DocumentNotFoundException => Future.failed(throw new DocumentNotFoundException(null))
        }
      })
    }
  }

  def updateDocumentByID(articleUpdate: UpdateArticle): Future[Unit] = {
    couchInstance({ db =>
      Future {
        try {
          val articleRepo = new ArticleRepository(db)
          val oldDocument = articleRepo.get(articleUpdate.id)
          oldDocument.name = articleUpdate.name.getOrElse(oldDocument.getName)
          oldDocument.description = articleUpdate.description.getOrElse(oldDocument.getDescription)
          oldDocument.text_pt = Some(articleUpdate.text_pt.getOrElse(oldDocument.getText_pt))
          oldDocument.text_en = Some(articleUpdate.text_en.getOrElse(oldDocument.getText_en))
          oldDocument.tags = articleUpdate.tags.getOrElse(oldDocument.tags)
          oldDocument.updated_at = Some(LocalDateTime.now().toString)
          db.update(oldDocument)
          Future.successful()
        } catch {
          case _: org.ektorp.DocumentNotFoundException => Future.failed(throw new DocumentNotFoundException(null))
          case ex: Throwable => Future.failed(new Exception(ex))
        }
      }
    })
  }

  def getUserByUsername(username:String): User = {
  couchInstance({db =>
    val newUserRepo = new UserRepository(db)
    val user = newUserRepo.findByUsername(username)
    user
  })
  }
}

