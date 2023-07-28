package front.model

import Routes.{About, AuthLogin, Locations}

enum Routes:

  case About, Locations, AuthLogin

  def toNavLabel: String =
    this match
      case About => "about"
      case Locations => "locations"
      case AuthLogin => "authlogin"

  def toUrlPath: String =
    this match
      case About => "/about"
      case Locations => "/locations"
      case AuthLogin => "/authlogin"