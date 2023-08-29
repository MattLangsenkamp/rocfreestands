package com.rocfreestands.server.config
import ciris.*
import cats.effect.*
import cats.syntax.all.*
import smithy.api.NonEmptyString

object FlywayConfig:
  case class FlywayConfig(
      url: String,
      user: Option[String],
      password: Option[Array[Char]],
      migrationsTable: String,
      migrationsLocations: List[String]
  )

  given flywayConfig: ConfigValue[IO, FlywayConfig] =
    (
      env("URL").as[String].default("jdbc:postgresql://localhost/rocfreestands"),
      env("USER").as[String].default("rocfreestands").option,
      env("PSQL_PASSWORD").as[String].default("password").map(_.toCharArray).option,
      env("MIGRATIONS_TABLE").as[String].default("flyway"),
      env("MIGRATIONS_LOCATIONS").as[String].default("db").map(_.split(",").toList)
    ).parMapN(FlywayConfig.apply)
