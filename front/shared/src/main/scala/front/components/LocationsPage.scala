package front.components

import front.model.{Model, Msg, NewLocationStep, Styles}
import tyrian.{Cmd, Html}
import tyrian.Html.*
import tyrian.syntax.*

def locationsPage: Model => Html[Msg] = (model: Model) =>
  div(Styles.mapStyle, cls := "p-2 pb-12 m-auto md:w-10/12 w-full h-96")(
    div(
      Styles.mapStyle,
      id  := "map",
      cls := "w-full max-h-full",
      onDrag(
        Msg.NoOp
      )
    )(
      model.newLocationStep match
        case Some(step) =>
          step match
            case NewLocationStep.LocationSelection =>
              div(Styles.z1000Style, cls := "absolute top-5 right-5")(
                mapButton("Cancel", Msg.CancelLocationSelection)(model),
                mapButton("Add Location Details", Msg.CompleteLocationSelection)(model)
              )
            case NewLocationStep.AddDetails =>
              div(Styles.z1000Style, cls := "absolute top-5 right-5 w-1/2 flex flex-col justify-end")(
                div(cls := "flex flex-row justify-end mb-2")(
                  mapButton("Edit Location Position", Msg.CancelAddDetails)(model),
                  mapButton("Add New Location", Msg.SubmitNewLocationForm)(model)
                ),
                newLocationPane(model)
              )
        case None =>
          div(Styles.z1000Style, cls := "absolute top-5 right-5")(
            mapButton("Add New Location", Msg.AddNewLocation)(model)
          )
    )
  )
