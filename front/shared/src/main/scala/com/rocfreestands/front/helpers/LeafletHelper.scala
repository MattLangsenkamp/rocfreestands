package com.rocfreestands.front.helpers

import com.rocfreestands.front.model.LocationForm.{LocationForm, ValidatedLocationForm, validateLocationForm}
import com.rocfreestands.core.{Location, Locations}
import com.rocfreestands.front.model.{MapLocation, Model, Msg}
import org.scalajs.dom.HTMLElement
import typings.leaflet.mod as L
import typings.leaflet.mod.{IconOptions, Icon_, Marker_}
import tyrian.Html.div
import tyrian.Html
import tyrian.Html.*
import tyrian.Cmd.Emit
import org.scalajs.dom.Element


object LeafletHelper:

  val TileLayerUri =
    "https://tile.openstreetmap.org/{z}/{x}/{y}.png"

  def init(model: Model): L.Map_ =
    val m = L.map("map").setView(L.LatLngLiteral(43.1521, -77.607649), zoom = 13)
    val tl = L
      .tileLayer(
        TileLayerUri,
        L.TileLayerOptions()
          .setId("mapbox.streets")
          .setMaxZoom(19)
          .setAttribution(
            """Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors,
              |<a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>,
              |Imagery © <a href="http://mapbox.com">Mapbox</a>""".stripMargin
          )
      )
      .addTo(m)

    addLocations(m, model.locations)

  def addLocation(map: L.Map_, location: MapLocation): L.Map_ =
    location.marker.addTo(map)
    map

  def addLocations(map: L.Map_, locations: List[MapLocation]): L.Map_ =
    locations.foldRight(map)((l, m) => addLocation(m, l))

  def locationToMapLocation(location: Location): MapLocation =
    val p = L.popup(L.PopupOptions().setContent(setContent(location)))
    val marker = L
      .marker(
        L.LatLngLiteral(location.latitude, location.longitude),
        L.MarkerOptions().setTitle(location.name)
      )
      .bindPopup(p)
    MapLocation(location, marker)

  private def setContent(location: Location): String =
    val url =
      f"https://www.google.com/maps/search/?api=1&query=${location.latitude},${location.longitude}"
    f"""<div class=\"inline-block break-words w-64\">
       |       <div class=\"flex justify-around\">
       |           <img src=\"${location.image}\" class=\"max-h-60 rounded\">
       |       </div>
       |       <div class="text-lg w-64 flex font-semibold text-indigo-400">${location.name}</div>
       |       <div style="border-bottom-width: 4px; padding-bottom: 8px;">${location.description}</div>
       |       <div>${location.address}</div>
       |       <a href="$url" target="_blank">
       |          <span
       |            title="google"
       |            class="text-lg w-64 flex font-semibold text-indigo-400 hover:text-indigo-200"
       |          >
       |            Google maps
       |            <svg
       |              xmlns="http://www.w3.org/2000/svg"
       |              fill="none"
       |              stroke="currentColor"
       |              viewBox="0 0 24 24"
       |              width="15"
       |              height="15"
       |            >
       |              <path
       |                stroke-linecap="round"
       |                stroke-linejoin="round"
       |                stroke-width="2"
       |                d="M10 6H6a2 2 0 00-2 2v10a2 2 0 002 2h10a2 2 0 002-2v-4M14 4h6m0 0v6m0-6L10 14"
       |              />
       |            </svg>
       |          </span>
       |        </a>
       |        <button class="update-button" id="update-button" style="display: none;" onclick="
       |        const updateLoc = new CustomEvent('update-requested', {
       |          bubbles: true,
       |          detail: '${location.uuid}',
       |           },
       |           );
       |        this.dispatchEvent(updateLoc);">Delete</button>
       |   </div>""".stripMargin

  def newLocationSelectionMarker(map: L.Map_): L.Marker_[Any] =

    val iconOptions = L
      .IconOptions(
        "./assets/marker-icon-green.png"
      )
      .setShadowUrl("https://cdnjs.cloudflare.com/ajax/libs/leaflet/0.7.7/images/marker-shadow.png")
      .setIconSize(L.point(25, 41))
      .setIconAnchor(L.point(12, 41))
      .setPopupAnchor(L.point(1, -34))
      .setShadowSize(L.point(41, 41))

    val icon = L.icon(iconOptions)
    val opts = L
      .MarkerOptions()
      .setDraggable(true)
      .setTitle("New Location")
      .setIcon(icon)
    L.marker(map.getCenter(), opts)
