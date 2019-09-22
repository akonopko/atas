package com.atlas.app

import java.sql.Timestamp

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import slick.jdbc.PostgresProfile
import slick.jdbc.PostgresProfile.api._
import spray.json.{DefaultJsonProtocol, DeserializationException, JsNumber, JsObject, JsValue, RootJsonFormat}

import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.{ClassTag, classTag}

class Database(implicit ec: ExecutionContext) extends SprayJsonSupport with DefaultJsonProtocol {

  implicit object TimestampFormat extends RootJsonFormat[Timestamp] {
    def write(obj: Timestamp) = {
      JsObject(
        "timestamp" -> JsNumber(obj.getTime)
      )
    }

    def read(json: JsValue) = {
      json.asJsObject().getFields("timestamp") match {
        case Seq(JsNumber(time)) => new Timestamp(time.toLong)
        case _ => throw new DeserializationException("Date expected")
      }
    }
  }


  val db = Database.forConfig("database")

  class Tokens(tag: Tag) extends Table[Token](tag, "TOKENS") {
    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)

    def value = column[String]("NAME")

    def identity = column[String]("IDENTITY")

    def createdAt = column[Long]("CREATED_AT")

    def expiresAt = column[Long]("EXPIRES_AT")

    def * = (id, value, identity, createdAt, expiresAt) <> (Token.tupled, Token.unapply)
  }

  val tokens: TableQuery[Tokens] = TableQuery[Tokens]


  class Landmarks(tag: Tag) extends Table[Landmark](tag, "LANDMARKS") {
    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)

    def name = column[String]("NAME")

    def longitude = column[String]("LONGITUDE")

    def latitude = column[String]("LATITUDE")

    def * = (id, name, longitude, latitude) <> (Landmark.tupled, Landmark.unapply)
  }

  val landmarks = TableQuery[Landmarks]


  class Cities(tag: Tag) extends Table[City](tag, "CITIES") {
    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)

    def name = column[String]("NAME")

    def * = (id, name) <> (City.tupled, City.unapply)
  }

  val cityTable = TableQuery[Cities]

  class Features(tag: Tag) extends Table[Feature](tag, "FEATURES") {
    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)

    def name = column[String]("NAME")

    def * = (id, name) <> (Feature.tupled, Feature.unapply)
  }

  val features = TableQuery[Features]

  class Users(tag: Tag) extends Table[User](tag, "OWNERS") {
    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)

    def name = column[String]("NAME")

    def phone = column[String]("PHONE")

    def email = column[String]("EMAIL")

    def isOwner = column[Boolean]("IS_OWNER")

    def ownerComment = column[Option[String]]("OWNER_COMMENT")

    def tenantComment = column[Option[String]]("TENANT_COMMENT")

    def * = (id, name, phone, email, isOwner, ownerComment, tenantComment) <> (User.tupled, User.unapply)
  }

  val users = TableQuery[Users]

  class Locations(tag: Tag) extends Table[Location](tag, "LOCATIONS") {
    def id = column[Int]("ID", O.PrimaryKey)

    def cityId = column[Int]("CITY_ID")

    def name = column[String]("NAME")

    def longitude = column[String]("LONGITUDE")

    def latitude = column[String]("LATITUDE")

    def address = column[String]("ADDRESS")

    def ownerId = column[Int]("OWNER_ID")

    def * = (id, cityId, name, longitude, latitude, address, ownerId) <> (Location.tupled, Location.unapply)

    def city_fk = foreignKey("CITY_FK", cityId, cityTable)(_.id)

    def owner_fk = foreignKey("OWNER_FK", ownerId, users)(_.id)
  }

  val locations = TableQuery[Locations]

  class Rooms(tag: Tag) extends Table[Room](tag, "ROOMS") {
    def id = column[Int]("ID", O.PrimaryKey)

    def locationId = column[Int]("LOCATION_ID")

    def name = column[String]("NAME")

    def size = column[Int]("SIZE")

    def comment = column[String]("COMMENT")

    def * = (id, locationId, name, size, comment.?) <> (Room.tupled, Room.unapply)

    def city_fk = foreignKey("LOCATION_FK", locationId, locations)(_.id)
  }

  val rooms = TableQuery[Rooms]


  class UserPhotos(tag: Tag) extends Table[UserPhoto](tag, "USER_PHOTOS") {
    def id = column[Int]("ID", O.PrimaryKey)

    def userId = column[Int]("USER_ID")

    def uri = column[String]("URI")

    def * = (id, userId, uri) <> (UserPhoto.tupled, UserPhoto.unapply)
  }

  val userPhotos = TableQuery[UserPhotos]

  class LocationPhotos(tag: Tag) extends Table[LocationPhoto](tag, "LOCATION_PHOTOS") {
    def id = column[Int]("ID", O.PrimaryKey)

    def locationId = column[Int]("LOCATION_ID")

    def uri = column[String]("URI")

    def * = (id, locationId, uri) <> (LocationPhoto.tupled, LocationPhoto.unapply)
  }

  val locationPhotos = TableQuery[LocationPhotos]


  class LocationFeatures(tag: Tag) extends Table[LocationFeature](tag, "LOCATION_FEATURES") {
    def id = column[Int]("ID", O.PrimaryKey)

    def locationId = column[Int]("LOCATION_ID")

    def featureId = column[Int]("FEATURE_ID")

    def * = (id, locationId, featureId) <> (LocationFeature.tupled, LocationFeature.unapply)
  }

  val locationFeatures = TableQuery[LocationFeatures]

  class RoomPhotos(tag: Tag) extends Table[RoomPhoto](tag, "ROOM_PHOTOS") {
    def id = column[Int]("ID", O.PrimaryKey)

    def roomId = column[Int]("ROOM_ID")

    def uri = column[String]("URI")

    def * = (id, roomId, uri) <> (RoomPhoto.tupled, RoomPhoto.unapply)
  }

  val roomPhotos = TableQuery[RoomPhotos]


  class RoomFeatures(tag: Tag) extends Table[RoomFeature](tag, "ROOM_FEATURES") {
    def id = column[Int]("ID", O.PrimaryKey)

    def roomId = column[Int]("ROOM_ID")

    def featureId = column[Int]("FEATURE_ID")

    def * = (id, roomId, featureId) <> (RoomFeature.tupled, RoomFeature.unapply)
  }

  val roomFeatures = TableQuery[RoomFeatures]


  class Rules(tag: Tag) extends Table[Rule](tag, "RULES") {
    def id = column[Int]("ID", O.PrimaryKey) // This is the primary key column
    def tenantId = column[Int]("TENANT_ID")

    def ruleStart = column[Timestamp]("RULE_START")

    def ruleEnd = column[Timestamp]("RULE_END")

    def visitStart = column[Timestamp]("VISIT_START")

    def visitEnd = column[Timestamp]("VISIT_END")

    def `type` = column[String]("TYPE")

    def * = (id, tenantId, ruleStart, ruleEnd, visitStart, visitEnd, `type`) <> (Rule.tupled, Rule.unapply)
  }

  val rules = TableQuery[Rules]

  class Visits(tag: Tag) extends Table[Visit](tag, "VISITS") {
    def id = column[Int]("ID", O.PrimaryKey) // This is the primary key column
    def ruleId = column[Int]("RULE_ID")

    def visitStart = column[Timestamp]("VISIT_START")

    def visitEnd = column[Timestamp]("VISIT_END")

    def paid = column[Boolean]("PAID")

    def * = (id, ruleId, visitStart, visitEnd, paid) <> (Visit.tupled, Visit.unapply)
  }

  val visits = TableQuery[Visits]

  class RoomRules(tag: Tag) extends Table[RoomRule](tag, "ROOM_RULES") {
    def id = column[Int]("ID", O.PrimaryKey)

    def roomId = column[Int]("ROOM_ID")

    def ruleId = column[Int]("RULE_ID")

    def * = (id, roomId, ruleId) <> (RoomRule.tupled, RoomRule.unapply)
  }

  val roomRules = TableQuery[RoomRules]

  val tables = List(
    tokens,
    landmarks,
    cityTable,
    features,
    users,
    userPhotos,
    locations,
    rooms,
    locationPhotos,
    locationFeatures,
    roomPhotos,
    roomFeatures,
    rules,
    visits,
    roomRules)

  val schema = tables.map(_.schema).reduce(_ ++ _)

  case class EntityContext[T](classTag: ClassTag[T],
                              filter: Int => Query[Table[T], T, Seq],
                              insert: PostgresProfile.IntoInsertActionComposer[T, T],
                              upsert: PostgresProfile.ReturningInsertActionComposer[T, T],
                              formatter: RootJsonFormat[T])

  val entities: Map[String, EntityContext[_ <: Entity]] = Map(
    "city" -> EntityContext(
      classTag[City],
      ids => cityTable.filter(_.id === ids),
      cityTable returning cityTable.map(_.id) into ((entity, id) => entity.copy(id = id)),
      cityTable returning cityTable,
      jsonFormat2(City)),
    "landmark" -> EntityContext(
      classTag[Landmark],
      ids => landmarks.filter(_.id === ids),
      landmarks returning landmarks.map(_.id) into ((entity, id) => entity.copy(id = id)),
      landmarks returning landmarks,
      jsonFormat4(Landmark)),
    "feature" -> EntityContext(
      classTag[Feature],
      ids => features.filter(_.id === ids),
      features returning features.map(_.id) into ((entity, id) => entity.copy(id = id)),
      features returning features,
      jsonFormat2(Feature)),
    "location" -> EntityContext(
      classTag[Location],
      ids => locations.filter(_.id === ids),
      locations returning locations.map(_.id) into ((entity, id) => entity.copy(id = id)),
      locations returning locations,
      jsonFormat7(Location)),
    "user" -> EntityContext(
      classTag[User],
      ids => users.filter(_.id === ids),
      users returning users.map(_.id) into ((entity, id) => entity.copy(id = id)),
      users returning users,
      jsonFormat7(User)),
    "userPhoto" -> EntityContext(
      classTag[UserPhoto],
      ids => userPhotos.filter(_.id === ids),
      userPhotos returning userPhotos.map(_.id) into ((entity, id) => entity.copy(id = id)),
      userPhotos returning userPhotos,
      jsonFormat3(UserPhoto)),
    "locationPhoto" -> EntityContext(
      classTag[LocationPhoto],
      ids => locationPhotos.filter(_.id === ids),
      locationPhotos returning locationPhotos.map(_.id) into ((entity, id) => entity.copy(id = id)),
      locationPhotos returning locationPhotos,
      jsonFormat3(LocationPhoto)),
    "locationFeature" -> EntityContext(
      classTag[LocationFeature],
      ids => locationFeatures.filter(_.id === ids),
      locationFeatures returning locationFeatures.map(_.id) into ((entity, id) => entity.copy(id = id)),
      locationFeatures returning locationFeatures,
      jsonFormat3(LocationFeature)),
    "roomPhoto" -> EntityContext(
      classTag[RoomPhoto],
      ids => roomPhotos.filter(_.id === ids),
      roomPhotos returning roomPhotos.map(_.id) into ((entity, id) => entity.copy(id = id)),
      roomPhotos returning roomPhotos,
      jsonFormat3(RoomPhoto)),
    "roomFeature" -> EntityContext(
      classTag[RoomFeature],
      ids => roomFeatures.filter(_.id === ids),
      roomFeatures returning roomFeatures.map(_.id) into ((entity, id) => entity.copy(id = id)),
      roomFeatures returning roomFeatures,
      jsonFormat3(RoomFeature)),
    "roomRule" -> EntityContext(
      classTag[RoomRule],
      ids => roomRules.filter(_.id === ids),
      roomRules returning roomRules.map(_.id) into ((entity, id) => entity.copy(id = id)),
      roomRules returning roomRules,
      jsonFormat3(RoomRule)),
    "room" -> EntityContext(
      classTag[Room],
      ids => rooms.filter(_.id === ids),
      rooms returning rooms.map(_.id) into ((entity, id) => entity.copy(id = id)),
      rooms returning rooms,
      jsonFormat5(Room)),
    "rule" -> EntityContext(
      classTag[Rule],
      ids => rules.filter(_.id === ids),
      rules returning rules.map(_.id) into ((entity, id) => entity.copy(id = id)),
      rules returning rules,
      jsonFormat7(Rule)),
    "visit" -> EntityContext(
      classTag[Visit],
      ids => visits.filter(_.id === ids),
      visits returning visits.map(_.id) into ((entity, id) => entity.copy(id = id)),
      visits returning visits,
      jsonFormat5(Visit)),
    "token" -> EntityContext(
      classTag[Token],
      ids => tokens.filter(_.id === ids),
      tokens returning tokens.map(_.id) into ((entity, id) => entity.copy(id = id)),
      tokens returning tokens,
      jsonFormat5(Token))
  )

  def getEntity[T <: Entity](id: Int, ctx: EntityContext[T])(implicit evidence: ClassTag[_ <: Entity]): Future[Seq[T]] = {
    val action = ctx.filter(id).result
    db.run(action)
  }

  def createEntity[T <: Entity](entity: T, ctx: EntityContext[T])(implicit evidence: ClassTag[_ <: Entity]): Future[T] = {
    val action = ctx.insert += entity
    db.run(action)
  }


  def updateEntity[T <: Entity](entity: T, ctx: EntityContext[T])(implicit evidence: ClassTag[_ <: Entity]): Future[Boolean] = {
    val action = ctx.upsert.insertOrUpdate(entity)
    db.run(action).map(_.isEmpty)
  }

  def deleteEntity[T <: Entity](id: Int, ctx: EntityContext[T])(implicit evidence: ClassTag[_ <: Entity]): Future[Boolean] = {
    val action = ctx.filter(id).delete
    db.run(action).map(_ > 0)
  }

  def dropDatabase() = {
    db.run(schema.drop)
  }

  def createDatabase() = {
    db.run(schema.create)
  }


}