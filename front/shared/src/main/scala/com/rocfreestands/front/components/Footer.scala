package com.rocfreestands.front.components

import com.rocfreestands.front.model.{Model, Msg, Routes}
import tyrian.Html
import tyrian.Html._

def footerComponent: Model => Html[Msg] = (model: Model) =>
  div(
    cls := "flex h-16 w-full justify-around content-center items-center bg-gray-100",
    id  := "footer"
  )(
    a(href := "https://github.com/rocfoodnotbombs", cls := "h-10", target := "_blank")(
      span()(),
      img(cls := "w-auto p-1 h-10", src := "../assets/git.svg")
    ),
    div(cls := "block font-thin text-xs xs:text-sm")(
      div(cls := "text-indigo-500")(
        text("This data set is made available under the"),
        div()(
          a(
            cls    := "text-indigo-600",
            rel    := "noopener noreferrer",
            target := "_blank",
            href   := "http://www.opendatacommons.org/licenses/pddl/1.0/"
          )("Public Domain Dedication and License v1.0")
        )
      )
    )
  )
