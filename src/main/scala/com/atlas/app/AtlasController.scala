package com.atlas.app

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes.OK
import akka.http.scaladsl.server.Directives.{entity, _}
import akka.http.scaladsl.server.Route
import com.atlas.app.Utils.nowUtc
import spray.json._

import scala.concurrent.ExecutionContext
import scala.reflect.ClassTag

class AtlasController(db: Database)
                     (implicit ec: ExecutionContext) extends SprayJsonSupport with DefaultJsonProtocol {


  //  ул. Марии Капнист, 8
  //  50.457668, 30.434722
  //  https://freshome.com/wp-content/uploads/2018/09/contemporary-exterior.jpg


  //  просп. Победы, 45
  //  50.453588, 30.448009
  //  https://assets.architecturaldesigns.com/plan_assets/324992268/large/23703JD_01_1553616680.jpg


  //  вулиця Терещенківська, 17
  //  50.440932, 30.515283
  //  https://freshome.com/wp-content/uploads/2018/09/contemporary-exterior.jpg


  import db._

  implicit val identityF = jsonFormat1(Identity)

  def getCtx[T](name: String)(implicit t: ClassTag[T]): T = {
    entities.get(name) match {
      case Some(value: T) => value
    }
  }


  private val authenticate = {
    val ctx = getCtx[EntityContext[Token]]("token")
    implicit val formatter = ctx.formatter

    (post & pathPrefix("authenticate") & end & entity(as[Identity])) { identity =>
      onSuccess {
        val uuid = java.util.UUID.randomUUID().toString
        val token = Token(0, uuid, identity.email, nowUtc.getMillis, nowUtc.plusHours(1).getMillis)
        db.createEntity(token, ctx)(ctx.classTag)
      } { newEntity =>
        complete(OK -> newEntity.toJson)
      }
    }
  }

  private val dropDatabase =
    (post & pathPrefix("admin" / "dropdb") & end) {
      onSuccess {
        db.dropDatabase()
      } {
        complete(OK -> "Success")
      }
    }

  private val createDatabase =
    (post & pathPrefix("admin" / "createdb") & end) {
      onSuccess {
        db.createDatabase()
      } {
        complete(OK -> "Success")
      }
    }


  def entityRoute(entities: Map[String, EntityContext[_ <: Entity]]) = {
    def createEntityEndpoint[T <: Entity](name: String, ctx: db.EntityContext[T])(implicit evidence: ClassTag[_ <: Entity]) = {
      implicit val formatter = ctx.formatter
      implicit val insertCtx = ctx.insert
      val createEndpoint = (post & pathPrefix("admin" / "entity" / name) & end & entity(as[T])) { e =>
        onSuccess {
          db.createEntity(e, ctx)(evidence)
        } { newEntity =>
          complete(OK -> newEntity.toJson)
        }
      }
      val getEndpoint = (get & pathPrefix("admin" / "entity" / name / IntNumber) & end) { id =>
        onSuccess {
          db.getEntity(id, ctx)(evidence)
        } { result =>
          complete(OK -> result.toJson)
        }
      }
      val deleteEndpoint = (delete & pathPrefix("admin" / "entity" / name / IntNumber) & end) { id =>
        onSuccess {
          db.deleteEntity(id, ctx)(evidence)
        } { result =>
          complete(OK -> result.toJson)
        }
      }
      val updateEndpoint = (put & pathPrefix("admin" / "entity" / name / IntNumber) & end & entity(as[T])) { case (id, entity) =>
        onSuccess {
          require(entity.getId == id, "provided entity should have same id as in url")
          db.updateEntity(entity, ctx)(evidence)
        } { result =>
          complete(OK -> result.toJson)
        } 
      }
      List(createEndpoint, getEndpoint, deleteEndpoint, updateEndpoint)
    }

    entities.flatMap {
      case (name, ctx) => {
        createEntityEndpoint(name, ctx)(ctx.classTag)
      }
    }
  }


  val route: Route = {
    val routes = entityRoute(entities) ++ List(authenticate, createDatabase, dropDatabase)
    routes.foldLeft[Route](reject)(_ ~ _)
  }

}
