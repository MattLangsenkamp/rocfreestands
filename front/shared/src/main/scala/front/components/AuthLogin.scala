package front.components

import front.model.{LoginForm, Model, Msg, Routes, Styles}
import tyrian.Html
import tyrian.Html.*
import tyrian.syntax.*
import tyrian.Cmd

def authLogin: Model => Html[Msg] = (model: Model) =>

  val loginForm = model.loginForm

  val usernameErrorMessage =
    loginForm.usernameErrorMessage.map(div(cls := "pl-7 text-red-600/75")(_)).orEmpty

  val passwordErrorMessage =
    loginForm.passwordErrorMessage.map(div(cls := "pl-7 text-red-600/75")(_)).orEmpty

  div(cls := "border border-gray-200  rounded w-3/4 md:w-1/2  lg:w-1/3 m-auto")(
    div(cls := "p-2 sm:flex w-full")(text("Username"), usernameErrorMessage),
    input(
      cls         := Styles.inputFormClasses,
      placeholder := "Enter Username",
      onInput(username => Msg.UpdateLoginForm(loginForm.copy(username = Some(username).filter(_.nonEmpty))))
    ),
    div(cls := "p-2 sm:flex w-full")(text("Password"), passwordErrorMessage),
    input(
      cls         := Styles.inputFormClasses,
      placeholder := "Enter Password",
      onInput(password => Msg.UpdateLoginForm(loginForm.copy(password = Some(password).filter(_.nonEmpty))))
    ),
    button(
      cls := "p-2 m-2 bg-white border focus:border-2 rounded text-gray-500 focus:text-gray-900 border-indigo-500 ml-2 hover:bg-gray-100 focus:outline-none",
      onClick(Msg.SubmitLoginForm(loginForm))
    )("Log In")
  )
