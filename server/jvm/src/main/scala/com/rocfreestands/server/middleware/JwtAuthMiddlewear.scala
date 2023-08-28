package com.rocfreestands.server.middleware

import cats.effect.IO
import smithy4s.http4s.ServerEndpointMiddleware
import com.rocfreestands.server.config.ServerConfig
import com.rocfreestands.server.services.AuthServiceImpl.AuthPayload
import io.circe.parser.decode
import org.http4s.{Credentials, HttpApp, Response, Status}
import smithy4s.Hints
import org.http4s.headers.`Authorization`
import pdi.jwt.{JwtAlgorithm, JwtCirce}
import smithy.api.HttpBearerAuth

object JwtAuthMiddlewear:
  private def middlewear(config: ServerConfig): HttpApp[IO] => HttpApp[IO] = { inputApp =>
    HttpApp[IO] { request =>
      (
        for
          token <- request.cookies.find(_.name == "Authorization")
          claim <- JwtCirce.decode(token.content, config.jwtSecretKey, Seq(JwtAlgorithm.HS256)).toOption
          payload <- decode[AuthPayload](claim.content).toOption
        yield
          if payload.user == config.username then inputApp(request)
          else IO(Response(Status.Unauthorized))
      ).getOrElse(IO(Response(Status.Unauthorized)))
    }
  }

  def fromServerConfig(config: ServerConfig): ServerEndpointMiddleware.Simple[IO] =
    (serviceHints: Hints, endpointHints: Hints) =>
      (serviceHints.get[smithy.api.HttpBasicAuth], serviceHints.get[smithy.api.HttpBearerAuth]) match
        case (None, Some(_)) =>
          endpointHints.get[smithy.api.Auth] match {
            case Some(auths) if auths.value.isEmpty => identity
            case Some(_)                            => middlewear(config)
            case _                                  => identity
          }
        case _ =>
          identity
