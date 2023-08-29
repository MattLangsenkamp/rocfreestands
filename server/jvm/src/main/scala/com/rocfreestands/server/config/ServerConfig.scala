package com.rocfreestands.server.config
import ciris.*
import cats.effect.*
import cats.syntax.all.*
import smithy.api.NonEmptyString

object ServerConfig:
  case class ServerConfig(
      username: String,
      password: String,
      psqlUsername: String,
      psqlPassword: String,
      picturePath: String,
      port: String,
      jwtSecretKey: String
  )

  given serverConfig: ConfigValue[IO, ServerConfig] =
    (
      env("RFS_USERNAME").as[String].default("admin"),
      env("RFS_PASSWORD").as[String].default("admin"),
      env("PSQL_USERNAME").as[String].default("rocfreestands"),
      env("PSQL_PASSWORD").as[String].default("password"),
      env("PICTURE_PATH").as[String].default("pictures"),
      env("RFS_PORT").as[String].default("8081"),
      env("JWT_SECRET_KEY").as[String].default("secret")
    ).parMapN(ServerConfig.apply)
