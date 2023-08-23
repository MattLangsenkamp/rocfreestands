package com.rocfreestands.server
import alloy.SimpleRestJson
import cats.data.Validated
import cats.effect.*
import cats.implicits.*
import com.comcast.ip4s.*
import com.rocfreestands.core.{
  DeleteLocationOutput,
  Location,
  LocationInput,
  Locations,
  LocationsService
}
import org.http4s.{HttpRoutes, Uri}
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.headers.Origin
import org.http4s.server.middleware.CORS
import smithy4s.{ByteArray, Timestamp}
import smithy4s.http4s.SimpleRestJsonBuilder
import com.rocfreestands.server.database.{FlywayConfig, SkunkSession}
import com.rocfreestands.server.services.{LocationsRepository, ObjectStore, fromPath, fromSession, make}
import fly4s.core.*
import fly4s.core.data.{
  BaselineResult,
  Fly4sConfig,
  MigrateResult,
  ValidatedMigrateResult,
  Location as MigrationLocation
}
import fly4s.implicits.*

import java.nio.file.{Files, Path}
import scala.util.Try
object Main extends IOApp.Simple:

  val dbConfig: FlywayConfig = FlywayConfig(
    url = "jdbc:postgresql://localhost/rocfreestands",
    user = Some("rocfreestands"),
    password = Some("password".toCharArray),
    migrationsTable = "flyway",
    migrationsLocations = List("db")
  )

  val fly4sRes: Resource[IO, MigrateResult] = Fly4s
    .make[IO](
      url = dbConfig.url,
      user = dbConfig.user,
      password = dbConfig.password,
      config = Fly4sConfig(
        table = dbConfig.migrationsTable,
        locations = dbConfig.migrationsLocations.map(s => MigrationLocation(s))
      )
    )
    .evalTap(_.baseline)
    .evalMap(_.migrate)

  private object Routes:

    private val policy = CORS.policy
      .withAllowOriginHost(
        Set(
          Origin.Host(Uri.Scheme.http, Uri.RegName("localhost"), Some(8080)),
          Origin.Host(Uri.Scheme.http, Uri.RegName("localhost"), Some(5173)),
          Origin.Host(Uri.Scheme.http, Uri.RegName("127.0.0.1"), Some(8080)),
          Origin.Host(Uri.Scheme.http, Uri.RegName("127.0.0.1"), Some(8081))
        )
      )

    private def makeRoutes(
        lr: LocationsRepository[IO],
        os: ObjectStore[IO]
    ): Resource[IO, HttpRoutes[IO]] =
      SimpleRestJsonBuilder.routes(make(lr, os)).resource

    private val docs: HttpRoutes[IO] =
      smithy4s.http4s.swagger.docs[IO](LocationsService)

    def all(lr: LocationsRepository[IO], os: ObjectStore[IO]): Resource[IO, HttpRoutes[IO]] =
      makeRoutes(lr, os).map(_ <+> docs).map(policy.apply(_))

  private def createFolderIfNotExist(path: Path): IO[Path] =
    if Files.exists(path) then IO(path)
    else IO.blocking(Files.createDirectory(path))

  def run: IO[Unit] =
    val s = for
      flyway         <- fly4sRes
      psqlConnection <- SkunkSession.skunkSession
      psqlSession    <- psqlConnection
      p              <- createFolderIfNotExist(Path.of("pictures")).toResource
      im             <- fromPath(p).toResource
      db             <- fromSession(psqlSession).toResource
      routes         <- Routes.all(db, im)
      srv <- EmberServerBuilder
        .default[IO]
        .withPort(port"8081")
        .withHttpApp(routes.orNotFound)
        .build
    yield srv

    s.evalMap(srv => IO.println(f"server running at ${srv.addressIp4s}")).useForever
