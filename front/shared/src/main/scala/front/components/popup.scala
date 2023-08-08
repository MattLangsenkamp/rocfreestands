package front.components

import front.model.{Model, Msg, PopupModel, Styles}
import tyrian.Html
import tyrian.Html.*
import tyrian.syntax.*

def popup(popupModel: PopupModel): Html[Msg] = div(Styles.popupStyle, cls := Styles.popupClasses)(
  div(popupModel.message),
  button(onClick(popupModel.msg), cls := Styles.mapButtonClasses)(popupModel.closeMessage)
)
