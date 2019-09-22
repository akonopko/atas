package com.atlas.app

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer

import scala.concurrent.ExecutionContext

object Application extends App {

  implicit val actorSystem: ActorSystem = ActorSystem()
  implicit val executionContext: ExecutionContext = actorSystem.dispatcher
  implicit val actorMaterializer: ActorMaterializer = ActorMaterializer()

  val db = new Database()
  val controller = new AtlasController(db)

  Http().bindAndHandle(controller.route, Settings.Http.host, Settings.Http.port)
}
