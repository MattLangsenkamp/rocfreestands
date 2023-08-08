package com.rocfreestands.front.helpers

import tyrian.cmds.*
import cats.effect.IO
import com.rocfreestands.front.model.{LoginForm, Msg}
import tyrian.Cmd
object AuthHelper:

  def updateLoginForm(lf: LoginForm): (LoginForm, Boolean) =
    lf match
      case LoginForm(Some(_), Some(_), _, _) =>
        (lf.copy(usernameErrorMessage = None, passwordErrorMessage = None), true)
      case LoginForm(Some(_), None, _, _) =>
        (
          lf.copy(usernameErrorMessage = None, passwordErrorMessage = Some("Password cannot be empty")),
          false
        )
      case LoginForm(None, Some(_), _, _) =>
        (
          lf.copy(usernameErrorMessage = Some("Username cannot be empty"), passwordErrorMessage = None),
          false
        )
      case LoginForm(None, None, _, _) =>
        (
          lf.copy(
            usernameErrorMessage = Some("Username cannot be empty"),
            passwordErrorMessage = Some("Password cannot be empty")
          ),
          false
        )
