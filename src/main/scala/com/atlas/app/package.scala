package com.atlas

package object app {

  import akka.http.scaladsl.server.Directives._

  def end = pathEndOrSingleSlash
}
