package com.rocfreestands.front.model

import com.rocfreestands.front.model.LocationForm.{LocationForm, LocationFormErrors}
import com.rocfreestands.core.Locations
import typings.leaflet.mod as L

case class Model(
    curPage: Routes,
    desc: String,
    footer: String,
    locations: List[MapLocation],
    map: Option[L.Map_],
    loginForm: LoginForm,
    authStatus: AuthStatus,
    newLocationStep: Option[NewLocationStep],
    newLocationForm: LocationForm,
    newLocationFormErrors: LocationFormErrors,
    newLocationMarker: Option[L.Marker_[Any]],
    popupModel: Option[PopupModel]
)

case class LoginForm(
    username: Option[String],
    password: Option[String],
    usernameErrorMessage: Option[String],
    passwordErrorMessage: Option[String]
)

enum NewLocationStep:
  case LocationSelection
  case AddDetails

case class AuthStatus(
    jwt: Option[String],
    signedIn: Boolean
)
object Model:
  def init(): Model =
    Model(
      Routes.Locations,
      "help",
      "",
      locations = List(),
      None,
      LoginForm(None, None, None, None),
      AuthStatus(None, false),
      None,
      LocationForm(),
      LocationFormErrors(),
      None,
      None    )
