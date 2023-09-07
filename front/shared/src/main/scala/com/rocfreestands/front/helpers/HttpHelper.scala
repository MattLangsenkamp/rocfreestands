package com.rocfreestands.front.helpers

import typings.leaflet.mod as L
import alloy.SimpleRestJson
import cats.effect.IO
import com.rocfreestands.front.model.LocationForm.LocationForm
import org.http4s.circe.CirceEntityCodec.*
import org.http4s.dom.FetchClientBuilder
import smithy4s.http4s.SimpleRestJsonBuilder
import org.http4s.Uri
import com.rocfreestands.core.{AuthService, AuthedLocationsService, Location, PublicLocationsService}
import com.rocfreestands.front.model.LocationForm.LocationForm
import com.rocfreestands.front.model.{LocationForm, Model, Msg}
import org.http4s.implicits.uri
import tyrian.Cmd
import io.circe.generic.auto.*
import org.http4s.circe.CirceEntityCodec.*
import org.scalajs.dom.{RequestCredentials, RequestMode}
import smithy4s.ByteArray

object HttpHelper:

  private case class Address(display_name: String)

  private val mode = scalajs.js.`import`.meta.env.MODE.asInstanceOf[String]
  private val clientUri =
    if mode == "development" then uri"http://127.0.0.1:8081/" else uri"https://rocfreestands.com/api"

  private val client = FetchClientBuilder[IO]
    .withMode(RequestMode.cors)
    .withCredentials(RequestCredentials.include)
    .create

  private val addressClient = FetchClientBuilder[IO]
    .create

  private val pubLocs =
    SimpleRestJsonBuilder(PublicLocationsService).client(client).uri(clientUri).use
  private val auth =
    SimpleRestJsonBuilder(AuthService).client(client).uri(clientUri).use
  private val authedLocs =
    SimpleRestJsonBuilder(AuthedLocationsService).client(client).uri(clientUri).use

  def createLocation(
      nlf: LocationForm,
      marker: L.Marker_[Any]
  ): Cmd[IO, Msg] =
    Cmd.Run {
      authedLocs
        .map { c =>
          val latLng = marker.getLatLng()
          c.createLocation(
            nlf.address,
            nlf.name,
            nlf.description,
            latLng.lat,
            latLng.lng,
            nlf.image
          ).map(l => Msg.OnAddLocationSuccess(LeafletHelper.locationToMapLocation(l)))
        }
        .getOrElse(IO.pure(Msg.NoOp))
    }

  def getLocations: Cmd[IO, Msg] =
    val io = pubLocs match
      case Left(_) =>
        IO.pure(Msg.NoOp)
      case Right(c) =>
        c.getLocations()
          .map(l => Msg.AddLocationsToMap(l.locations.map(LeafletHelper.locationToMapLocation)))
    Cmd.Run(io)

  def deleteLocation(uuid: String): Cmd[IO, Msg] =
    val io = authedLocs
      .map(_.deleteLocation(uuid).map(resp => Msg.OnDeleteLocationSuccess(resp.message, uuid)))
      .getOrElse(IO.pure(Msg.OnDeleteLocationFailure("failed to instantiate client")))
    Cmd.Run(io)

  def login(username: String, password: String): Cmd[IO, Msg] =
    val io = auth
      .map(_.login(username, password).map(resp => Msg.OnLoginSuccess))
      .getOrElse(IO(Msg.OnLoginError))
    Cmd.Run(io)

  def refresh: Cmd[IO, Msg] =
    val io = auth
      .map(_.refresh().map(_ => Msg.SetLoggedIn(true)))
      .getOrElse(IO(Msg.SetLoggedIn(false)))
    Cmd.Run(io)
  def getLocationAddress(model: Model): Cmd[IO, Msg] =
    Cmd.Run {
      model.newLocationMarker
        .map { marker =>
          val uri =
            f"https://nominatim.openstreetmap.org/reverse?format=jsonv2&lat=${marker.getLatLng().lat}&lon=${marker.getLatLng().lng}"
          for
            address <- addressClient.expect[Address](uri).attempt
            msg <- IO {
              address
                .map(addr => Msg.ResolvePlaceHolderAddress(addr.display_name))
                .getOrElse(Msg.ResolvePlaceHolderAddress(""))
            }
          yield msg
        }
        .getOrElse(IO.pure(Msg.ResolvePlaceHolderAddress("")))
    }
