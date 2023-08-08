package com.rocfreestands.front.model

import com.rocfreestands.core.Location
import typings.leaflet.mod as L
case class MapLocation(location: Location, marker: L.Marker_[Any])
