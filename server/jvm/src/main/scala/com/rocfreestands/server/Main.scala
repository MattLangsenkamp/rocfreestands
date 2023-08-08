package com.rocfreestands.server
import alloy.SimpleRestJson
import cats.effect.*
import cats.implicits.*
import com.rocfreestands.core.{DeleteLocationOutput, Location, LocationInput, Locations, LocationsService}
import org.http4s.{HttpRoutes, Uri}
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.headers.Origin
import org.http4s.server.middleware.CORS
import smithy4s.{ByteArray, Timestamp}
import smithy4s.http4s.SimpleRestJsonBuilder
import com.rocfreestands.server.DB

import scala.util.Try
object Main extends IOApp.Simple:

  val impl: LocationsService[IO] = new LocationsService[IO] {

    override def getLocations(): IO[Locations] =
      IO.println("getting locations") *>
        IO.pure(DB.locs)

    override def createLocation(
        address: String,
        name: String,
        description: String,
        latitude: Double,
        longitude: Double,
        image: ByteArray
    ): IO[Location] =
      IO.println("creating location") *>
        IO.blocking(
          DB.addToLocs(address, name, description, latitude, longitude, image)
        )

    override def deleteLocation(uuid: String): IO[DeleteLocationOutput] =
      IO.blocking(
        DeleteLocationOutput(
          DB.deleteLoc(uuid)
            .map(_ => "Location Deleted")
            .getOrElse("Could Not Find Requested Location")
        )
      )
  }

  private object Routes:

    private val policy = CORS.policy
      .withAllowOriginHost(
        Set(
          Origin.Host(Uri.Scheme.http, Uri.RegName("localhost"), Some(8080)),
          Origin.Host(Uri.Scheme.http, Uri.RegName("localhost"), Some(5173)),
          Origin.Host(Uri.Scheme.http, Uri.RegName("127.0.0.1"), Some(8080))
        )
      )

    private val r: Resource[IO, HttpRoutes[IO]] =
      SimpleRestJsonBuilder.routes(impl).resource

    private val docs: HttpRoutes[IO] =
      smithy4s.http4s.swagger.docs[IO](LocationsService)

    val all: Resource[IO, HttpRoutes[IO]] = r.map(_ <+> docs).map(policy.apply(_))

  def run: IO[Unit] =
    Routes.all
      .flatMap { routes =>
        EmberServerBuilder
          .default[IO]
          .withHttpApp(routes.orNotFound)
          .build
      }
      .evalMap(srv => IO.println(srv.addressIp4s))
      .useForever
