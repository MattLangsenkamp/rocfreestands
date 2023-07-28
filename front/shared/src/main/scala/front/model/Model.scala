package front.model

import typings.leaflet.mod as L


case class Model(
    curPage: Routes,
    desc: String,
    footer: String,
    locations: List[Location],
    map: Option[L.Map_],
    loginForm: LoginForm,
    authStatus: AuthStatus,
    newLocationStep: Option[NewLocationStep],
    newLocationForm: LocationForm,
    newLocationMarker: Option[L.Marker_[Any]],
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
      locations = (1 to 5).toList.map(_ => Location.randLocation()),
      None,
      LoginForm(None, None, None, None),
      AuthStatus(None, false),
      None,
      Location.newLocationForm(),
      None,
    )
