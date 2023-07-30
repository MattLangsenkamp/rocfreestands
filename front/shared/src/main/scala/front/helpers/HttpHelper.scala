package front.helpers
import typings.leaflet.mod as L

import alloy.SimpleRestJson
import cats.effect.IO
import org.http4s.circe.CirceEntityCodec.*
import org.http4s.dom.FetchClientBuilder
import smithy4s.http4s.SimpleRestJsonBuilder
import org.http4s.Uri
import hello.LocationsService
import front.model.{LocationForm, Msg}
import org.http4s.implicits.uri
import tyrian.Cmd
object HttpHelper:

  private val client = FetchClientBuilder[IO].create
  private val r =
    SimpleRestJsonBuilder(LocationsService).client(client).uri(uri"http://127.0.0.1:8080/").use

  def createLocation(
      nlf: LocationForm,
      marker: L.Marker_[Any]
  ): Cmd[IO, Msg] =
    val io: IO[Msg] = r match
      case Left(_) =>
        IO.pure(Msg.NoOp)
      case Right(c) =>
        val latLng      = marker.getLatLng()
        val name        = nlf.name.getOrElse("No Name")
        val description = nlf.description.getOrElse("NoDescription")
        c.createLocation("", name, description, latLng.lat, latLng.lng)
          .map(l => Msg.OnAddLocationSuccess(l))

    Cmd.Run(io)

  def getLocations: Cmd[IO, Msg] =
    val io = r match
      case Left(_) =>
        IO.pure(Msg.NoOp)
      case Right(c) =>
        c.getLocations().map(l => Msg.AddLocationsToMap(l))

    Cmd.Run(io)
