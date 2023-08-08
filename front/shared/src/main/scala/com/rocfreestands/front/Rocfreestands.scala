package com.rocfreestands.front

import com.rocfreestands.front.model.LocationForm.{LocationForm, LocationFormErrors, validateLocationForm}
import cats.effect.IO
import cats.effect.IO.asyncForIO
import cats.data.Validated.*
import com.rocfreestands.front.components.*
import com.rocfreestands.front.model.{AuthStatus, LoginForm, MapLocation, Model, Msg, NewLocationStep, PopupModel, Routes, Styles}
import org.scalajs.dom
import org.scalajs.dom.{CustomEvent, MouseEvent, document, html}
import typings.leaflet.mod as L
import tyrian.*
import tyrian.Html.*
import tyrian.cmds.*
import tyrian.syntax.*
import com.rocfreestands.core.Locations
import com.rocfreestands.front.helpers.{AuthHelper, EffectHelper, HttpHelper, LeafletHelper}
import com.rocfreestands.front.model.{AuthStatus, LoginForm, Model, Msg, NewLocationStep, PopupModel, Routes, Styles}

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
    case Msg.ShowUpdateButtons =>
      (model, EffectHelper.showUpdateButtons())
    case Msg.AddLocationsToMap(locations) =>
      (
        model.copy(locations = model.locations ::: locations),
        EffectHelper.addLocationsToMap(model, locations)
      )
    case Msg.AddLocationToMap(location) =>
      println("nice")
      val m = (
        model.copy(locations = model.locations ::: location :: Nil),
        EffectHelper.addNewPermanentLocation(model, location)
      )
      println(m._1.locations)
      m
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
      println("innnnn")
      (
        model.copy(authStatus = AuthStatus(jwt = Some(jwt), signedIn = true)),
        Cmd.Batch(EffectHelper.setJWT(model, jwt), EffectHelper.showUpdateButtons())
      )
    case Msg.BeginAddNewLocation =>
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
        model,
        HttpHelper.getLocationAddress(model)
      )
    case Msg.ResolvePlaceHolderAddress(address) =>
      (
        model.copy(
          newLocationStep = Some(NewLocationStep.AddDetails),
          newLocationForm = model.newLocationForm.copy(address = address)
        ),
        Cmd.None
      )
    case Msg.CancelLocationSelection =>
      val removeMarkerSideEffect = EffectHelper.removeLocationSelectionMarker(model)
      (model.copy(newLocationStep = None, newLocationMarker = None), removeMarkerSideEffect)
    case Msg.UpdateLocationForm(locationForm) =>
      (model.copy(newLocationForm = locationForm), Cmd.None)
    case Msg.SubmitNewLocationForm =>
      validateLocationForm(model.newLocationForm) match
        case Valid(nf) =>
          model.newLocationMarker match
            case Some(marker) =>
              (model, HttpHelper.createLocation(nf, marker))
            case None => (model, Cmd.None)
        case Invalid(nef) => (model.copy(newLocationFormErrors = nef), Cmd.None)
    case Msg.CancelAddDetails =>
      (model.copy(newLocationStep = Some(NewLocationStep.LocationSelection)), Cmd.None)
    case Msg.OnAddLocationSuccess(loc) =>
      val removeMarkerSideEffect = EffectHelper.removeLocationSelectionMarker(model)
      (
        model.copy(
          newLocationStep = None,
          newLocationForm = LocationForm()
        ),
        Cmd.Batch(removeMarkerSideEffect, Cmd.emit(Msg.AddLocationToMap(loc)))
      )
    case Msg.OnAddLocationError => ???
    case Msg.ShowPopup(popupModel) =>
      (
        model.copy(popupModel = Some(popupModel)),
        Cmd.None
      )
    case Msg.ClosePopup =>
      (
        model.copy(popupModel = None),
        Cmd.None
      )
    case Msg.ProposeDeleteLocation(uuid) =>
      val loc = model.locations.find(_.location.uuid == uuid)
      loc
        .map { l =>
          (
            model.copy(
              popupModel = Some(
                PopupModel(
                  f"Do You Want To Delete ${l.location.name}",
                  "Delete",
                  Msg.TryDeleteLocation(l)
                )
              )
            ),
            Cmd.None
          )
        }
        .getOrElse(
          (
            model.copy(
              popupModel = Some(
                PopupModel(
                  f"Something Went Wrong Could Not Delete Location With ID: $uuid",
                  "Ok",
                  Msg.ClosePopup
                )
              )
            ),
            Cmd.None
          )
        )

    case Msg.TryDeleteLocation(mp) =>
      (
        model,
        HttpHelper.deleteLocation(mp.location.uuid)
      )
    case Msg.OnDeleteLocationFailure(message) =>
      (
        model.copy(
          popupModel = Some(
            PopupModel(
              message,
              "Ok",
              Msg.ClosePopup
            )
          )
        ),
        Cmd.None
      )
    case Msg.OnDeleteLocationSuccess(message, uuid) =>
      (
        model.copy(
          popupModel = Some(
            PopupModel(
              message,
              "Ok",
              Msg.ClosePopup
            )
          )
        ),
        EffectHelper.removeLocationMarker(model, uuid)
      )
    case Msg.RemoveMapLocationFromModel(uuid) =>
      (
        model.copy(
          locations = model.locations.filter(_.location.uuid != uuid)
        ),
        Cmd.None
      )
  def view(model: Model): Html[Msg] =

    val contents =
      model.curPage match
        case Routes.About     => aboutPage(model)
        case Routes.Locations => locationsPage(model)
        case Routes.AuthLogin => authLogin(model)

    // top level container
    div(cls := Styles.containerClasses, id := "container")(
      model.popupModel.map(popup).orEmpty,
      headerComponent(model),
      contents,
      footerComponent(model)
    )

  def catchDeleteReq(model: Model): Sub[IO, Msg] =
    Sub.fromEvent("update-requested", document) { case e: CustomEvent =>
      Option(Msg.ProposeDeleteLocation(e.detail.asInstanceOf[String]))
    }

  def subscriptions(model: Model): Sub[IO, Msg] =
    Sub.Batch(
      catchDeleteReq(model),
      Sub.every[IO](0.2.second).map { _ =>
        model.curPage match
          case Routes.Locations => Msg.ShowUpdateButtons
          case _                => Msg.NoOp
      },
      Sub.every[IO](0.2.second).map { _ =>
        model.curPage match
          case Routes.Locations => Msg.RenderMap
          case _                => Msg.NoOp
      }
    )
