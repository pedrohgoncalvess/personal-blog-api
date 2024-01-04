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


  def getAllDocuments: Future[Array[Map[String, Any]]] = {
    Future {
      couchInstance { db =>
        val newQueryView = new ViewQuery().allDocs().includeDocs(true)
        val result = db.queryView(newQueryView)
        val rawDocuments = result.getRows
        rawDocuments.toArray.map(row => jsonStrToMap(row.toString))
      }
    }
  }

  def getDocumentsByTag(tag: String): Array[Map[String, Serializable]] = {
    couchInstance { db =>
      val articleRepo = new ArticleRepository(db)
      val articlesToReturn = articleRepo.findByTag(tag.toLowerCase())
      if (articlesToReturn.isEmpty) {
        throw new DocumentNotFoundException(null)
      }
      val mapsToReturn = articlesToReturn.map(article =>
        Map(
          "name" -> article.name,
          "description" -> article.description,
          "text_en" -> article.text_en.getOrElse("null"),
          "text_pt" -> article.text_pt.getOrElse("null"),
          "updated_at" -> article.updated_at.getOrElse("null"),
          "tags" -> article.tags
        ))
      mapsToReturn.toArray
    }
  }

  def getDocumentByID(id: String): Article = {
    couchInstance { db =>
      val document = db.get(classOf[java.util.Map[String, AnyRef]], id)
      val rawTagsOfDoc = document.get("tags")
      val tagsOfDoc: Array[String] = rawTagsOfDoc match {
        case seqTag: java.util.ArrayList[String] => seqTag.asScala.toArray
        case _ => Array.empty[String]
      }
      Article(
        id = Some(document.getOrDefault("_id", null).toString),
        revision = Some(document.getOrDefault("_rev", null).toString),
        name = document.getOrDefault("name", null).toString,
        description = document.getOrDefault("description", null).toString,
        text_en = Some(document.getOrDefault("text_en", null).toString),
        text_pt = Some(document.getOrDefault("text_pt", null).toString),
        tags = tagsOfDoc,
        updated_at=Some(document.getOrDefault("updated_at",null).toString)
      )
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

