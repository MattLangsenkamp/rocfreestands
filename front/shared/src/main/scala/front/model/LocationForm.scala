package front.model
import cats.{Semigroup, data}
import cats.syntax.all.*
import cats.data.*
import cats.data.Validated.*

object LocationForm:

  case class LocationForm(
      name: String = "",
      description: String = "",
      address: String = "",
      image: String = ""
  )

  case class LocationFormErrors(
      nameErrorMessage: Option[String] = None,
      descriptionErrorMessage: Option[String] = None,
      addressErrorMessage: Option[String] = None,
      imageErrorMessage: Option[String] = None
  )

  given locationFormErrorsSemigroup: Semigroup[LocationFormErrors] =
    (x: LocationFormErrors, y: LocationFormErrors) =>
      LocationFormErrors(
        x.nameErrorMessage |+| y.nameErrorMessage,
        x.descriptionErrorMessage |+| y.descriptionErrorMessage,
        x.addressErrorMessage |+| y.addressErrorMessage,
        x.imageErrorMessage |+| y.imageErrorMessage
      )

  type ValidatedLocationForm[A] = Validated[LocationFormErrors, A]

  private def validateName(form: LocationForm): ValidatedLocationForm[String] =
    if form.name.matches("^[0-9a-zA-Z- ]+") then Valid(form.name)
    else if form.name.isEmpty then
      Invalid(LocationFormErrors(nameErrorMessage = Some("Name cannot be empty")))
    else
      Invalid(
        LocationFormErrors(nameErrorMessage = Some("Name must be alpha-numeric characters only"))
      )

  private def validateDescription(form: LocationForm): ValidatedLocationForm[String] =
    if form.description.matches("^[0-9a-zA-Z- !?_@.]+") then Valid(form.description)
    else
      Invalid(
        LocationFormErrors(descriptionErrorMessage =
          Some("Description must be alpha-numeric characters or (!?_@.)")
        )
      )

  private def validateAddress(form: LocationForm): ValidatedLocationForm[String] =
    if form.address.nonEmpty then Valid(form.address)
    else Invalid(LocationFormErrors(addressErrorMessage = Some("Address cannot be empty")))

  private def validateImage(form: LocationForm): ValidatedLocationForm[String] =
    if form.image.nonEmpty then Valid(form.image)
    else Invalid(LocationFormErrors(imageErrorMessage = Some("Image cannot be empty")))

  def validateLocationForm(form: LocationForm): ValidatedLocationForm[LocationForm] =
    (validateName(form), validateDescription(form), validateAddress(form), validateImage(form)).mapN(
      LocationForm.apply
    )
