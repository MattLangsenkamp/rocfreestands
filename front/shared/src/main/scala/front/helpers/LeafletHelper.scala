package front.helpers

import front.model.{LocationForm, Model, Msg}
import hello.{Location, Locations}
import typings.leaflet.mod as L
import typings.leaflet.mod.{IconOptions, Icon_, Marker_}
import tyrian.Html.div
import tyrian.Cmd.Emit

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
    // m.on_dragend(typings.leaflet.leafletStrings.dragend, e=>
    //  println("dragged")
    //  Emit(Msg.NoOp)
    // )
    addLocations(m, model.locations)

  def addLocation(map: L.Map_, location: Location): L.Map_ =
    val p = L.popup(L.PopupOptions().setContent(setContent(location)))
    L.marker(
      L.LatLngLiteral(location.latitude, location.longitude),
      L.MarkerOptions().setTitle(location.name)
    ).bindPopup(p)
      .addTo(map)
    map

  def addLocations(map: L.Map_, locations: Locations): L.Map_ =
    locations.locations.foldRight(map)((l, m) => addLocation(m, l))

  // hover:text-indigo-200
  private def setContent(location: Location): String =
    val url =
      f"https://www.google.com/maps/search/?api=1&query=${location.latitude},${location.longitude}"
    f"""<div class=\"inline-block break-words w-64\">
    |       <div class=\"flex justify-around\">
    |           <img src=\"https://picsum.photos/200/300\" class=\"max-h-60 rounded\">
    |       </div>
    |       <div class=\"text-lg w-64 flex font-semibold text-indigo-400 \">${location.name}</div>
    |       <div class=\"\">${location.description}</div>
    |       <div class=\"\">${location.address}</div>
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

  def updateLocationForm(lf: LocationForm): (LocationForm, Boolean) =
    // TODO refactor using monads/cats later
    lf match
      case LocationForm(Some(name), Some(description), Some(image), _, _, _) =>
        (
          lf.copy(nameErrorMessage = None, descriptionErrorMessage = None, imageErrorMessage = None),
          true
        )
      case LocationForm(Some(name), None, None, _, _, _) =>
        (
          lf.copy(
            nameErrorMessage = None,
            descriptionErrorMessage = Some("Description cannot be empty"),
            imageErrorMessage = Some("Image cannot be empty")
          ),
          false
        )
      case LocationForm(None, Some(description), None, _, _, _) =>
        (
          lf.copy(
            nameErrorMessage = Some("Name cannot be empty"),
            descriptionErrorMessage = None,
            imageErrorMessage = Some("Image cannot be empty")
          ),
          false
        )
      case LocationForm(None, None, Some(image), _, _, _) =>
        (
          lf.copy(
            nameErrorMessage = Some("Name cannot be empty"),
            descriptionErrorMessage = Some("Description cannot be empty"),
            imageErrorMessage = None
          ),
          false
        )
      case LocationForm(Some(name), Some(description), None, _, _, _) =>
        (
          lf.copy(
            nameErrorMessage = None,
            descriptionErrorMessage = None,
            imageErrorMessage = Some("Image cannot be empty")
          ),
          false
        )
      case LocationForm(Some(name), None, Some(image), _, _, _) =>
        (
          lf.copy(
            nameErrorMessage = None,
            descriptionErrorMessage = Some("Description cannot be empty"),
            imageErrorMessage = None
          ),
          false
        )
      case LocationForm(None, Some(description), Some(image), _, _, _) =>
        (
          lf.copy(
            nameErrorMessage = Some("Name cannot be empty"),
            descriptionErrorMessage = None,
            imageErrorMessage = None
          ),
          false
        )
      case LocationForm(None, None, None, _, _, _) =>
        (
          lf.copy(
            nameErrorMessage = Some("Name cannot be empty"),
            descriptionErrorMessage = Some("Description cannot be empty"),
            imageErrorMessage = Some("Image cannot be empty")
          ),
          false
        )
