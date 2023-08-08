package com.rocfreestands.front.components

import com.rocfreestands.front.model.{Model, Msg, Styles}
import tyrian.Html
import tyrian.Html.*
import tyrian.syntax.*
import tyrian.syntax.orEmpty

def newLocationPane: Model => Html[Msg] = (model: Model) =>

  val locationPaneStyles =
    """
      |border border-indigo-500 w-full max-h-fit bg-white p-1
      |rounded scrollbar-thin scrollbar-thumb-indigo-500
      |scrollbar-track-indigo-200 overflow-x-scroll overflow-y-scroll
      |""".stripMargin

  val previewImg =
    if model.newLocationForm.image.isEmpty then div()
    else img(src := model.newLocationForm.image, cls := "max-h-80 mb-2")

  val nameError =
    model.newLocationFormErrors.nameErrorMessage.map(div(cls := "pl-7 text-red-600/75")(_)).orEmpty

  val descriptionError =
    model.newLocationFormErrors.descriptionErrorMessage
      .map(div(cls := "pl-7 text-red-600/75")(_))
      .orEmpty

  val addressError =
    model.newLocationFormErrors.addressErrorMessage
      .map(div(cls := "pl-7 text-red-600/75")(_))
      .orEmpty

  val imageError =
    model.newLocationFormErrors.imageErrorMessage.map(div(cls := "pl-7 text-red-600/75")(_)).orEmpty

  div(Styles.z1000Style, Styles.vhStyle(65), cls := locationPaneStyles)(
    div(Styles.vhStyle(40), cls := "flex-row flex w-full")(
      // div(cls := "p-2 sm:flex w-full ")(text("Image"), ),
      div()(
        input(
          // cls := Styles.mapButtonClasses,
          `type` := "file",
          accept := "image/png, image/jpeg",
          id     := "image-upload",
          onInput(_ => Msg.LoadImageToLocationForm)
        ),
        imageError
      ),
      previewImg
    ),
    div(cls := "pt-5 flex-row flex")(
      div(cls := "p-2 sm:flex w-full")(text("Name"), nameError),
      input(
        cls         := Styles.inputFormClasses,
        placeholder := "Enter Username",
        onInput(name => Msg.UpdateLocationForm(model.newLocationForm.copy(name = name)))
      )
    ),
    div(cls := "pt-5 flex-row flex")(
      div(cls := "p-2 sm:flex w-full")(text("Description"), descriptionError),
      textarea(
        rows        := "3",
        cls         := Styles.inputFormClasses,
        placeholder := "Give a description for this stand",
        onInput(description =>
          Msg.UpdateLocationForm(model.newLocationForm.copy(description = description))
        )
      )()
    ),
    div(cls := "pt-5 flex-row flex")(
      div(cls := "p-2 sm:flex w-full")(text("Address"), addressError),
      input(
        cls         := Styles.inputFormClasses,
        placeholder := "Enter Address",
        value       := model.newLocationForm.address,
        onInput(address => Msg.UpdateLocationForm(model.newLocationForm.copy(address = address)))
      )
    )
  )
