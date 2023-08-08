package front.model
import hello.Location
import typings.leaflet.mod as L
case class MapLocation(location: Location, marker: L.Marker_[Any])
