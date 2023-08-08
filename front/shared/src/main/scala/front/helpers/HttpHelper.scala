package front.helpers
import typings.leaflet.mod as L
import alloy.SimpleRestJson
import cats.effect.IO
import front.model.LocationForm.LocationForm
import org.http4s.circe.CirceEntityCodec.*
import org.http4s.dom.FetchClientBuilder
import smithy4s.http4s.SimpleRestJsonBuilder
import org.http4s.Uri
import hello.{Location, LocationsService}
import front.model.{LocationForm, Model, Msg}
import org.http4s.implicits.uri
import tyrian.Cmd
import io.circe.generic.auto.*
import org.http4s.circe.CirceEntityCodec.*
object HttpHelper:

  private case class Address(display_name: String)

  private val client = FetchClientBuilder[IO].create
  private val r =
    SimpleRestJsonBuilder(LocationsService).client(client).uri(uri"http://127.0.0.1:8080/").use

  def createLocation(
      nlf: LocationForm,
      marker: L.Marker_[Any]
  ): Cmd[IO, Msg] =
    Cmd.Run {
      r.map { c =>
        val latLng = marker.getLatLng()
        c.createLocation(nlf.address, nlf.name, nlf.description, latLng.lat, latLng.lng)
          .map(l => Msg.OnAddLocationSuccess(LeafletHelper.locationToMapLocation(l)))
      }.getOrElse(IO.pure(Msg.NoOp))
    }

  def getLocations: Cmd[IO, Msg] =
    val io = r match
      case Left(_) =>
        IO.pure(Msg.NoOp)
      case Right(c) =>
        c.getLocations()
          .map(l => Msg.AddLocationsToMap(l.locations.map(LeafletHelper.locationToMapLocation)))

    Cmd.Run(io)

  def deleteLocation(uuid: String): Cmd[IO, Msg] =
    val io = r
      .map {
        _.deleteLocation(uuid).map(resp => Msg.OnDeleteLocationSuccess(resp.message, uuid))
      }
      .getOrElse(IO.pure(Msg.OnDeleteLocationFailure("failed to instantiate client")))

    Cmd.Run(io)

  def getLocationAddress(model: Model): Cmd[IO, Msg] =
    Cmd.Run {
      model.newLocationMarker
        .map { marker =>
          val uri =
            f"https://nominatim.openstreetmap.org/reverse?format=jsonv2&lat=${marker.getLatLng().lat}&lon=${marker.getLatLng().lng}"
          for
            address <- client.expect[Address](uri).attempt
            msg <- IO {
              address
                .map(addr => Msg.ResolvePlaceHolderAddress(addr.display_name))
                .getOrElse(Msg.ResolvePlaceHolderAddress(""))
            }
          yield msg
        }
        .getOrElse(IO.pure(Msg.ResolvePlaceHolderAddress("")))
    }
