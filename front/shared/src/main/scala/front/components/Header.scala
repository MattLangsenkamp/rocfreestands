package front.components

import front.model.{Model, Msg, Routes, Styles}
import tyrian.Html
import tyrian.Html.*

def headerComponent: Model => Html[Msg] =
  (m: Model) =>
    div(cls := "relative pt-6 px-4 sm:px-6 lg:px-8 mb-10", id := "header")(
      nav(cls := "relative flex items-center justify-between h-10", id := "nav-bar")(
        div(cls := "flex items-center flex-grow flex-shrink-0 lg:flex-grow-0")(
          div(cls := "flex items-center justify-between w-full md:w-auto")(
            a(href := "", onClick(Msg.JumpToLocations))(
              img(
                cls := "h-8 w-auto sm:h-10",
                src := "./assets/rocflower.svg"
              )
            )
          )
        ),
        div(cls := "block md:ml-10 md:pr-4 md:space-x-8")(
          button(
            cls := "pr-4 md:pr-0 font-medium text-gray-500 hover:text-gray-900",
            onClick(Msg.JumpToAbout)
          )(
            "about"
          ),
          button(
            cls := "font-medium text-gray-500 hover:text-gray-900",
            onClick(Msg.JumpToLocations)
          )("locations")
        )
      )
    )
