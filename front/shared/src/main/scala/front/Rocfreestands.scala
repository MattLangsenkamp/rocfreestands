package front

import cats.effect.IO
import cats.effect.IO.asyncForIO
import front.components.*
import front.helpers.{AuthHelper, EffectHelper, HttpHelper, LeafletHelper}
import front.model.{AuthStatus, LoginForm, Model, Msg, NewLocationStep, Routes, Styles, Location as Loc}
import org.scalajs.dom
import org.scalajs.dom.{Event, document, html}
import typings.leaflet.mod as L
import tyrian.*
import tyrian.Html.*
import tyrian.cmds.*
import hello.Locations
import scala.concurrent.duration.*
import scala.scalajs.js.JSConverters.*
import scala.scalajs.js.annotation.*

@JSExportTopLevel("TyrianApp")
object Rocfreestands extends TyrianApp[Msg, Model]:

  def router: Location => Msg =
    case loc: Location.Internal =>
      loc.pathName match
        case "/"          => Msg.JumpToLocations
        case "/about"     => Msg.JumpToAbout
        case "/locations" => Msg.JumpToLocations
        case "/authlogin" => Msg.JumpToAuthLogin
    case loc: Location.External =>
      Msg.NavigateToUrl(loc.href)

  private def consoleLog(msg: String): Cmd[IO, Nothing] =
    Cmd.SideEffect {
      println(msg)
    }

  def init(flags: Map[String, String]): (Model, Cmd[IO, Msg]) =
    (
      Model.init(),
      Cmd.Batch(Cmd.emit(Msg.CheckIfLoggedIn), HttpHelper.getLocations)
    )

  def update(
      model: Model
  ): Msg => (Model, Cmd[IO, Msg]) =
    case Msg.NoOp =>
      (model, Cmd.None)
    case Msg.JumpToAbout =>
      model.map match
        case Some(value) =>
          value.remove()
          (model.copy(curPage = Routes.About, map = None), Nav.pushUrl(Routes.About.toUrlPath))
        case None =>
          (model.copy(curPage = Routes.About), Nav.pushUrl(Routes.About.toUrlPath))
    case Msg.JumpToLocations =>
      (model.copy(curPage = Routes.Locations), Nav.pushUrl(Routes.Locations.toUrlPath))
    case Msg.JumpToAuthLogin =>
      (model.copy(curPage = Routes.AuthLogin), Nav.pushUrl(Routes.AuthLogin.toUrlPath))
    case Msg.NavigateToUrl(href) => (model, Nav.loadUrl(href))
    case Msg.RenderMap =>
      model.curPage match
        case Routes.Locations =>
          model.map match
            case Some(value) =>
              (model, Cmd.None)
            case None => (model.copy(map = Some(LeafletHelper.init(model))), Cmd.None)
        case _ => (model, Cmd.None)
    case Msg.AddLocationsToMap(locations) =>
      (
        model.copy(locations = Locations(model.locations.locations ::: locations.locations)),
        EffectHelper.addLocationsToMap(model, locations)
      )
    case Msg.AddLocationToMap(location) =>
      (
        model.copy(locations = Locations(model.locations.locations ::: location :: Nil)),
        EffectHelper.addNewPermanentLocation(model, location)
      )
    case Msg.LoadImageToLocationForm =>
      (model, EffectHelper.readImageToLocationForm(model))
    case Msg.UpdateLoginForm(f) =>
      (model.copy(loginForm = f), Cmd.None)
    case Msg.SubmitLoginForm(f) =>
      val (checkedForm, valid) = AuthHelper.updateLoginForm(f)
      if (valid)
        // todo network request to get JWT
        // should we clear login form only upon success?
        (model.copy(loginForm = LoginForm(None, None, None, None)), Cmd.Emit(Msg.OnLoginSuccess))
      else
        (model.copy(loginForm = checkedForm), Cmd.None)
    case Msg.OnLoginSuccess =>
      (model, Cmd.emit(Msg.SetJWT("loggedin")))
    case Msg.OnLoginError => ???
    case Msg.CheckIfLoggedIn =>
      (model, EffectHelper.getJWT(model))
    case Msg.SetJWT(jwt) =>
      (
        model.copy(authStatus = AuthStatus(jwt = Some(jwt), signedIn = true)),
        EffectHelper.setJWT(model, jwt)
      )
    // TODO rename this command, to reflect that it is just the start of the adding process
    case Msg.AddNewLocation =>
      val nextMessage = model.map match
        case Some(map) =>
          Cmd.emit {
            val m = LeafletHelper.newLocationSelectionMarker(map)
            m.addTo(map)
            Msg.AddNewLocationMarker(m)
          }
        case None => Cmd.None
      (
        model.copy(newLocationStep = Some(NewLocationStep.LocationSelection)),
        nextMessage
      )
    case Msg.AddNewLocationMarker(m) =>
      (model.copy(newLocationMarker = Some(m)), Cmd.None)
    case Msg.CompleteLocationSelection =>
      (
        model.copy(
          newLocationStep = Some(NewLocationStep.AddDetails),
          newLocationForm = model.newLocationForm
        ),
        Cmd.None
      )
    case Msg.CancelLocationSelection =>
      val removeMarkerSideEffect = EffectHelper.removeLocationSelectionMarker(model)
      (model.copy(newLocationStep = None, newLocationMarker = None), removeMarkerSideEffect)
    case Msg.UpdateLocationForm(locationForm) =>
      (model.copy(newLocationForm = locationForm), Cmd.None)
    case Msg.SubmitNewLocationForm =>
      val (nf, valid) = LeafletHelper.updateLocationForm(model.newLocationForm)
      if (valid)
        model.newLocationMarker match
          case Some(marker) =>
            (model, HttpHelper.createLocation(nf, marker))
          case None => (model, Cmd.None)
      else
        (model.copy(newLocationForm = nf), Cmd.None)
    case Msg.CancelAddDetails =>
      (model.copy(newLocationStep = Some(NewLocationStep.LocationSelection)), Cmd.None)
    case Msg.OnAddLocationSuccess(loc) =>
      val removeMarkerSideEffect = EffectHelper.removeLocationSelectionMarker(model)
      (
        model.copy(
          newLocationStep = None,
          newLocationForm = Loc.newLocationForm()
        ),
        Cmd.Batch(removeMarkerSideEffect, Cmd.emit(Msg.AddLocationToMap(loc)))
      )
    case Msg.OnAddLocationError => ???
  def view(model: Model): Html[Msg] =
    val contents =
      model.curPage match
        case Routes.About     => aboutPage(model)
        case Routes.Locations => locationsPage(model)
        case Routes.AuthLogin => authLogin(model)

    // top level container
    div(cls := Styles.containerClasses, id := "container")(
      headerComponent(model),
      contents,
      footerComponent(model)
    )

  def subscriptions(model: Model): Sub[IO, Msg] =
    Sub.Batch(
      Sub.every[IO](0.2.second).map { _ =>
        model.curPage match
          case Routes.Locations => Msg.RenderMap
          case _                => Msg.NoOp
      }
    )
