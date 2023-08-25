package com.rocfreestands.server.services
import dev.profunktor.auth.*
import dev.profunktor.auth.jwt.*
import pdi.jwt.*
import io.circe.parser.*
import cats.data.{Kleisli, OptionT}
import cats.effect.IO
import com.rocfreestands.server.config.ServerConfig
import com.rocfreestands.core.{AuthResponse, AuthService}
import io.circe.Decoder
import org.http4s.{AuthedRequest, AuthedRoutes, BasicCredentials, Request, Response, Status}
import org.http4s.headers.Authorization
import org.http4s.server.AuthMiddleware

import java.time.Instant

object AuthServiceImpl:

  private val algo    = JwtAlgorithm.HS256
  private val jwtAuth = (conf: ServerConfig) => JwtAuth.hmac(conf.jwtSecretKey, algo)

  case class AuthPayload(user: String)

  given decoder: Decoder[AuthPayload] = Decoder.instance: h =>
    for user <- h.get[String]("user")
    yield AuthPayload(user)

  private def makeClaim(username: String) = JwtClaim(
    content = f"""{"user":"$username"}""",
    expiration = Some(Instant.now.plusSeconds(157784760).getEpochSecond),
    issuedAt = Some(Instant.now.getEpochSecond)
  )
  def fromServerConfig(config: ServerConfig): AuthService[IO] = new AuthService[IO] {

    private def makeToken(username: String) =
      JwtCirce.encode(makeClaim(username), config.jwtSecretKey, algo)

    override def login(username: String, password: String): IO[AuthResponse] = IO(
      if username.equals(config.username) && password.equals(config.password) then
        AuthResponse(makeToken(username))
      else AuthResponse("")
    )
  }

  def makeAuthMiddleWear(config: ServerConfig): AuthMiddleware[IO, AuthPayload] =
    val authenticate: JwtToken => JwtClaim => IO[Option[AuthPayload]] =
      (token: JwtToken) =>
        (claim: JwtClaim) =>
          decode[AuthPayload](claim.content) match
            case Right(payload) =>
              IO.pure(
                Option.when(payload.user == config.username)(payload)
              )
            case Left(_) => IO(None)
    JwtAuthMiddleware[IO, AuthPayload](jwtAuth(config), authenticate)
