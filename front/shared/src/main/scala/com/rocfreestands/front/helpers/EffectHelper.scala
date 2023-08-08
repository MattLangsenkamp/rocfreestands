package com.rocfreestands.front.helpers

import tyrian.Cmd
import tyrian.cmds.{FileReader, LocalStorage}
import cats.effect.IO
import cats.effect.IO.asyncForIO
import com.rocfreestands.core.{Location, Locations}
import com.rocfreestands.front.model.{MapLocation, Model, Msg}
import org.scalajs.dom.html.Div
import org.scalajs.dom.document
import typings.leaflet.mod as L

object EffectHelper:

  def removeLocationSelectionMarker(model: Model): Cmd.SideEffect[IO] =
    Cmd.SideEffect {
      val removed = (model.map, model.newLocationMarker) match
        case (Some(map), Some(marker)) =>
          Some(marker.removeFrom(map))
        case _ =>
          None
    }

  def removeLocationMarker(model: Model, uuid: String): Cmd[IO, Msg] =
    Cmd.emit {
      (for
        ml  <- model.locations.find(_.location.uuid == uuid)
        map <- model.map
      yield ml.marker.removeFrom(map)).map(_ => Msg.RemoveMapLocationFromModel(uuid)).getOrElse(Msg.NoOp)
    }

  def addNewPermanentLocation(model: Model, loc: MapLocation): Cmd.SideEffect[IO] =
    Cmd.SideEffect {
      model.map.foreach(map => LeafletHelper.addLocation(map, loc))
    }

  def addLocationsToMap(model: Model, locs: List[MapLocation]): Cmd.SideEffect[IO] =
    Cmd.SideEffect {
      model.map.foreach(map => LeafletHelper.addLocations(map, locs))
    }
  def readImageToLocationForm(model: Model, idName: String = "image-upload"): Cmd[IO, Msg] =
    FileReader.readText(idName) {
      case FileReader.Result.Error(msg) =>
        Msg.NoOp
      case FileReader.Result.File(name, path, contents) =>
        Msg.UpdateLocationForm(model.newLocationForm.copy(image = contents))
    }

  def setJWT(model: Model, jwt: String, key: String = "jwt"): Cmd[IO, Msg] =
    LocalStorage.setItem(key, jwt) {
      case LocalStorage.Result.Success => Msg.JumpToLocations
      case e                           => Msg.NoOp
    }

  def getJWT(model: Model, key: String = "jwt"): Cmd[IO, Msg] =
    LocalStorage.getItem(key) {
      case Right(LocalStorage.Result.Found(value)) =>
        Msg.SetJWT(value)
      case Left(LocalStorage.Result.NotFound(e)) => Msg.NoOp
    }

  def showUpdateButtons(): Cmd[IO, Msg] =
    val updateButtons = document.getElementsByClassName("update-button")
    Cmd.SideEffect {
      updateButtons.foreach(elem => elem.asInstanceOf[Div].style.display = "block")
    }
