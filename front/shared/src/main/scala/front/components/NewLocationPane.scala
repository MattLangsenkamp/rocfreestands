package front.components

import front.model.{Model, Msg, Styles}
import tyrian.Html
import tyrian.Html.*
import tyrian.syntax.*

def newLocationPane: Model => Html[Msg] = (model: Model) =>

  val locationPaneStyles =
    """
      |border border-indigo-500 w-full max-h-fit bg-white p-1
      |rounded scrollbar-thin scrollbar-thumb-indigo-500
      |scrollbar-track-indigo-200 overflow-x-scroll overflow-y-scroll
      |""".stripMargin

  val previewImg =
    model.newLocationForm.image.map(base64 => img(src := base64, cls := "max-h-80 mb-2")).orEmpty

  val nameError =
    model.newLocationForm.nameErrorMessage.map(div(cls := "pl-7 text-red-600/75")(_)).orEmpty

  val descriptionError =
    model.newLocationForm.descriptionErrorMessage.map(div(cls := "pl-7 text-red-600/75")(_)).orEmpty

  val imageError =
    model.newLocationForm.imageErrorMessage.map(div(cls := "pl-7 text-red-600/75")(_)).orEmpty

  div(Styles.z1000Style, Styles.vhStyle(65), cls := locationPaneStyles)(
    div(Styles.vhStyle(40), cls := "flex-row flex w-full")(
      // div(cls := "p-2 sm:flex w-full ")(text("Image"), imageError),
      input(
        // cls := Styles.mapButtonClasses,
        `type` := "file",
        accept := "image/png, image/jpeg",
        id     := "image-upload",
        onInput(_ => Msg.LoadImageToLocationForm)
      ),
      previewImg
    ),
    div(cls := "pt-5 flex-row flex")(
      div(cls := "p-2 sm:flex w-full")(text("Name"), nameError),
      input(
        cls         := Styles.inputFormClasses,
        placeholder := "Enter Username",
        onInput(name =>
          Msg.UpdateLocationForm(model.newLocationForm.copy(name = Some(name).filter(_.nonEmpty)))
        )
      )
    ),
    div(cls := "pt-5 flex-row flex")(
      div(cls := "p-2 sm:flex w-full")(text("Description"), descriptionError),
      textarea(
        rows        := "3",
        cls         := Styles.inputFormClasses,
        placeholder := "Give a description for this stand",
        onInput(description =>
          Msg.UpdateLocationForm(
            model.newLocationForm.copy(description = Some(description).filter(_.nonEmpty))
          )
        )
      )()
    )
  )
