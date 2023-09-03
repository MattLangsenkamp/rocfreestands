package com.rocfreestands.front.model

import com.rocfreestands.core.{Location, Locations}
import com.rocfreestands.front.model.LocationForm.LocationForm
import typings.leaflet.mod as L
import com.rocfreestands.front.model.LocationForm.{LocationForm, LocationFormErrors}

enum Msg:
  case NoOp
  // navigation
  case JumpToAbout
  case JumpToLocations
  case JumpToAuthLogin
  case NavigateToUrl(href: String)

  // Map
  case AddLocationsToMap(locations: List[MapLocation])
  case AddLocationToMap(location: MapLocation)
  case RemoveMapLocationFromModel(uuid: String)
  case RenderMap
  case ShowUpdateButtons

  // Auth
  case UpdateLoginForm(f: LoginForm)
  case SubmitLoginForm(f: LoginForm)
  case OnLoginSuccess
  case OnLoginError
  case CheckIfLoggedIn
  case TryRefresh
  case SetLoggedIn(loggedIn: Boolean)

  // add location MSGs
  case BeginAddNewLocation // moves to location selection
  case AddNewLocationMarker(m: L.Marker_[Any])
  case CompleteLocationSelection // moves to details
  case CancelLocationSelection   // stops the add location process
  case ResolvePlaceHolderAddress(address: String)
  case LoadImageToLocationForm
  case UpdateLocationForm(locationForm: LocationForm)
  case SubmitNewLocationForm // attempts to persist the new location to the server
  case CancelAddDetails      // goes back to the location selection step
  case OnAddLocationSuccess(location: MapLocation)
  case OnAddLocationError

  // popup stuff
  case ShowPopup(popupModel: PopupModel)
  case ClosePopup

  // delete location
  case ProposeDeleteLocation(uuid: String)
  case TryDeleteLocation(location: MapLocation)
  case OnDeleteLocationSuccess(message: String, uuid: String)
  case OnDeleteLocationFailure(message: String)
