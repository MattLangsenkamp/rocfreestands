package front.model

import typings.leaflet.mod as L

enum Msg:
  case NoOp
  case JumpToAbout
  case JumpToLocations
  case JumpToAuthLogin
  case RenderMap
  case LoadImage
  case NavigateToUrl(href: String)
  case UpdateLoginForm(f: LoginForm)
  case SubmitLoginForm(f: LoginForm)
  case OnLoginSuccess
  case OnLoginError
  case CheckIfLoggedIn
  case SetJWT(jwt: String)
  // add location msgs
  case AddNewLocation // moves to location selection
  case AddNewLocationMarker(m: L.Marker_[Any])
  case CompleteLocationSelection // moves to details
  case CancelLocationSelection // stops the add location process
  case UpdateLocationForm(locationForm: LocationForm)
  case SubmitNewLocationForm // attempts to persist the new location to the server
  case CancelAddDetails // goes back to the location selection step
  case OnAddLocationSuccess
  case OnAddLocationError