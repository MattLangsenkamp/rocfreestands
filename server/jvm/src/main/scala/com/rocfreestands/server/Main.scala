package com.rocfreestands.server
import alloy.SimpleRestJson
import cats.data.Validated
import cats.effect.*
import cats.implicits.*
import com.comcast.ip4s.*
import com.rocfreestands.core.{AuthService, AuthedLocationsService, DeleteLocationOutput, Location, LocationInput, Locations, PublicLocationsService}
import com.rocfreestands.server.config.{FlywayConfig, ServerConfig}
import org.http4s.{HttpRoutes, Uri}
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.headers.Origin
import org.http4s.server.middleware.CORS
import smithy4s.{ByteArray, Timestamp}
import smithy4s.http4s.SimpleRestJsonBuilder
import com.rocfreestands.server.database.{Flyway, SkunkSession}
import com.rocfreestands.server.middleware.JwtAuthMiddlewear
import com.rocfreestands.server.services.{AuthServiceImpl, LocationsRepository, ObjectStore, fromPath, fromSession, makePublicLocationService}
import com.rocfreestands.server.services.AuthServiceImpl.{fromServerConfig, makeAuthMiddleWear}
import com.rocfreestands.server.services.AuthedLocationServiceImpl.makeAuthedLocationService
import fly4s.core.*
import fly4s.core.data.{BaselineResult, Fly4sConfig, MigrateResult, ValidatedMigrateResult, Location as MigrationLocation}
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

  private val serverConfig: ServerConfig = ServerConfig(
    username = "admin",
    password = "admin",
    psqlUsername = "rocfreestands",
    psqlPassword = "password",
    picturePath = "pictures",
    port = "8081",
    jwtSecretKey = "secret"
  )

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
        c: ServerConfig,
        lr: LocationsRepository[IO],
        os: ObjectStore[IO]
    ): Resource[IO, HttpRoutes[IO]] =
      val middleWear = makeAuthMiddleWear(c)
      for
        pubLocRoutes <- SimpleRestJsonBuilder
          .routes(makePublicLocationService(lr, os))
          .resource
        authLocRoutes <- SimpleRestJsonBuilder
          .routes(makeAuthedLocationService(lr, os))
          .middleware(JwtAuthMiddlewear.fromServerConfig(c))
          .resource
        authRoutes <- SimpleRestJsonBuilder.routes(fromServerConfig(c)).resource
      yield pubLocRoutes <+> authLocRoutes <+> authRoutes

    private val docs: HttpRoutes[IO] =
      smithy4s.http4s.swagger.docs[IO](AuthedLocationsService, AuthService, PublicLocationsService)
      //smithy4s.http4s.swagger.docs[IO](AuthedLocationsService) <+>
        //smithy4s.http4s.swagger.docs[IO](AuthService) <+>
        //smithy4s.http4s.swagger.docs[IO](PublicLocationsService)

    def all(
        c: ServerConfig,
        lr: LocationsRepository[IO],
        os: ObjectStore[IO]
    ): Resource[IO, HttpRoutes[IO]] =
      makeRoutes(c, lr, os).map(_ <+> docs).map(policy.apply(_))

  private def createFolderIfNotExist(path: Path): IO[Path] =
    if Files.exists(path) then IO(path)
    else IO.blocking(Files.createDirectory(path))

  def run: IO[Unit] =
    val s = for
      _              <- Flyway.runFlywayMigration(dbConfig)
      psqlConnection <- SkunkSession.skunkSession
      psqlSession    <- psqlConnection
      p              <- createFolderIfNotExist(Path.of("pictures")).toResource
      im             <- fromPath(p).toResource
      db             <- fromSession(psqlSession).toResource
      routes         <- Routes.all(serverConfig, db, im)
      srv <- EmberServerBuilder
        .default[IO]
        .withPort(Port.fromString(serverConfig.port).get)
        .withHttpApp(routes.orNotFound)
        .build
    yield srv

    s.evalMap(srv => IO.println(f"server running at ${srv.addressIp4s}")).useForever
