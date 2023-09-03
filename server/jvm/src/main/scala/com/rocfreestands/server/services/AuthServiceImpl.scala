package com.rocfreestands.server.services
import pdi.jwt.*
import io.circe.parser.*
import cats.data.{Kleisli, OptionT}
import cats.effect.IO
import com.rocfreestands.server.config.ServerConfig.ServerConfig
import com.rocfreestands.core.{AuthService, AuthResponse}
import io.circe.Decoder
import org.http4s.{AuthedRequest, AuthedRoutes, BasicCredentials, Request, Response, Status}
import org.http4s.headers.Authorization
import org.http4s.server.AuthMiddleware

import java.time.Instant
import scala.util.{Failure, Success}

object AuthServiceImpl:

  private val algo = JwtAlgorithm.HS256

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

    def makeToken(username: String) =
      f"Authorization=${JwtCirce.encode(makeClaim(username), config.jwtSecretKey, algo)}"

    override def refresh(): IO[AuthResponse] =
      IO(AuthResponse("Successfully Refreshed Token", Some(makeToken(config.username))))

    override def login(username: String, password: String): IO[AuthResponse] = IO(
      if username.equals(config.username) && password.equals(config.password) then
        AuthResponse(message = "Login Successful", cookie = Some(makeToken(username)))
      else AuthResponse("Login Failed")
    )
  }
