package com.rocfreestands.server.services

import cats.data.{Kleisli, OptionT}
import cats.effect.IO
import com.rocfreestands.server.config.ServerConfig
import com.rocfreestands.core.{AuthResponse, AuthService}
import com.rocfreestands.server.model.LoggedInUser
import org.http4s.{AuthedRequest, AuthedRoutes, BasicCredentials, Request, Response, Status}
import org.http4s.headers.Authorization
import org.http4s.server.AuthMiddleware

object AuthServiceImpl:
  def fromServerConfig(config: ServerConfig): AuthService[IO] = new AuthService[IO] {
    override def login(username: String, password: String): IO[AuthResponse] = ???
  }

  val basicAuthMethod = Kleisli.apply[IO, Request[IO], Either[String, LoggedInUser]]: req =>
    val authHeader = req.headers.get[Authorization]
    authHeader match
      case Some(Authorization(BasicCredentials(creds))) => IO(Right(LoggedInUser(creds._1)))
      case Some(_)                                      => IO(Left("No Basic Credentials"))
      case None                                         => IO(Left("Unauthorized!!"))

  val onFailure: AuthedRoutes[String, IO] = Kleisli: (req: AuthedRequest[IO, String]) =>
    OptionT.pure[IO](Response[IO](status = Status.Unauthorized))

  val userBasicAuthMiddleware: AuthMiddleware[IO, LoggedInUser] = AuthMiddleware(basicAuthMethod, onFailure)