package com.rocfreestands.front.components

import com.rocfreestands.front.model.{Model, Msg, Styles}
import tyrian.Html
import tyrian.Html.*

def mapButton(buttonText: String, onClickMsg: Msg): Model => Html[Msg] = (model: Model) =>
  button(Styles.z1000Style, cls := Styles.mapButtonClasses, title := "title", onClick(onClickMsg))(
    text(buttonText)
  )
