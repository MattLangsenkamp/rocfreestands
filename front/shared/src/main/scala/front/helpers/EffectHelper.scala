package front.helpers

import tyrian.Cmd
import tyrian.cmds.{FileReader, LocalStorage}
import cats.effect.IO
import cats.effect.IO.asyncForIO
import front.model.{Model, Msg}
import hello.{Location, Locations}

object EffectHelper:

  def removeLocationSelectionMarker(model: Model): Cmd.SideEffect[IO] =
    Cmd.SideEffect {
      val removed = (model.map, model.newLocationMarker) match
        case (Some(map), Some(marker)) =>
          Some(marker.removeFrom(map))
        case _ =>
          None
    }

  def addNewPermanentLocation(model: Model, loc: Location): Cmd.SideEffect[IO] =
    Cmd.SideEffect {
      val added = model.map match
        case Some(map) => Some(LeafletHelper.addLocation(map, loc))
        case None      => None
    }

  def addLocationsToMap(model: Model, locs: Locations): Cmd.SideEffect[IO] =
    Cmd.SideEffect {
      val added = model.map match
        case Some(map) => Some(locs.locations.foreach(LeafletHelper.addLocation(map, _)))
        case None      => None
    }
  def readImageToLocationForm(model: Model, idName: String = "image-upload"): Cmd[IO, Msg] =
    FileReader.readText(idName) {
      case FileReader.Result.Error(msg) =>
        Msg.NoOp
      case FileReader.Result.File(name, path, contents) =>
        Msg.UpdateLocationForm(model.newLocationForm.copy(image = Some(contents)))
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
