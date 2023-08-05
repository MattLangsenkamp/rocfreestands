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
          .map(l => Msg.OnAddLocationSuccess(l))
      }.getOrElse(IO.pure(Msg.NoOp))
    }

  def getLocations: Cmd[IO, Msg] =
    val io = r match
      case Left(_) =>
        IO.pure(Msg.NoOp)
      case Right(c) =>
        c.getLocations().map(l => Msg.AddLocationsToMap(l))

    Cmd.Run(io)

  def getLocationAddress(model: Model): Cmd[IO, Msg] =
    Cmd.Run {
      println("ok!")
      model.newLocationMarker
        .map { marker =>
          println("yea!")
          val uri =
            f"https://nominatim.openstreetmap.org/reverse?format=jsonv2&lat=${marker.getLatLng().lat}&lon=${marker.getLatLng().lng}"
          println(uri)
          for
            address <- client.expect[Address](uri).attempt
            _       <- IO.println(address)
            msg <- IO {
              address
                .map(addr => Msg.ResolvePlaceHolderAddress(addr.display_name))
                .getOrElse(Msg.ResolvePlaceHolderAddress(""))
            }
          yield msg
        }
        .getOrElse(IO.pure(Msg.ResolvePlaceHolderAddress("")))
    }
