package front.helpers

import alloy.SimpleRestJson
import cats.effect.IO
import org.http4s.circe.CirceEntityCodec.*
import org.http4s.dom.FetchClientBuilder
import smithy4s.http4s.SimpleRestJsonBuilder
import org.http4s.Uri
import hello.LocationsService
import front.model.Msg
import org.http4s.implicits.uri
import tyrian.Cmd
object HttpHelper:

  private val client = FetchClientBuilder[IO].create
  private val r =
    SimpleRestJsonBuilder(LocationsService).client(client).uri(uri"http://127.0.0.1:8080/").use

  def createLocation(
      address: String,
      name: String,
      description: String,
      latitude: Double,
      longitude: Double
  ): Cmd[IO, Msg] =
    val io = r match
      case Left(value) =>
        println("failed!")
        IO.pure(Msg.NoOp)
      case Right(c) =>
        println("worked?")
        c.createLocation(address, name, description, latitude, longitude).map(l => Msg.NoOp)

    Cmd.Run(io)
