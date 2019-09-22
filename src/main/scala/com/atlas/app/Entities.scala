package com.atlas.app

import java.sql.Timestamp

class Entity(id: Int) {
  def getId = id
}

case class Identity(email: String)

case class Token(id: Int, value: String, identity: String, createdAt: Long, expiresAt: Long) extends Entity(id)

case class Landmark(id: Int, name: String, longitude: String, latitude: String) extends Entity(id)

case class City(id: Int, name: String) extends Entity(id)

case class Feature(id: Int, name: String) extends Entity(id)

case class Location(id: Int,
                    cityId: Int,
                    name: String,
                    longitude: String,
                    latitude: String,
                    address: String,
                    ownerId: Int) extends Entity(id)

case class User(id: Int, name: String, phone: String, email: String, isOwner: Boolean, ownerComment: Option[String], tenantComment: Option[String]) extends Entity(id)

case class UserPhoto(id: Int, userId: Int, uri: String) extends Entity(id)

case class LocationPhoto(id: Int, locationId: Int, uri: String) extends Entity(id)

case class LocationFeature(id: Int, locationId: Int, featureId: Int) extends Entity(id)

case class RoomPhoto(id: Int, roomId: Int, uri: String) extends Entity(id)

case class RoomFeature(id: Int, roomId: Int, featureId: Int) extends Entity(id)

case class RoomRule(id: Int, roomId: Int, ruleId: Int) extends Entity(id)

case class Room(id: Int,
                locationId: Int,
                name: String,
                size: Int,
                comment: Option[String]) extends Entity(id)

case class Rule(id: Int, tenantId: Int, ruleStart: Timestamp, ruleEnd: Timestamp, visitStart: Timestamp, visitEnd: Timestamp, `type`: String) extends Entity(id)

case class Visit(id: Int, ruleId: Int, visitStart: Timestamp, visitEnd: Timestamp, paid: Boolean) extends Entity(id)
