package front.components

import front.model.Model
import front.model.Msg
import front.model.Routes
import tyrian.Html
import tyrian.Html._

def aboutPage: Model => Html[Msg] = (model: Model) =>
  div(
    cls := "font-mono  text-gray-500 map p-2 pb-12 m-auto max-w-m md:max-w-3xl lg:max-w-5xl xl:max-w-7xl h-full"
  )(
    div(
      cls := "flex p-4 mb-8 justify-center scrollbar-thin scrollbar-thumb-indigo-500 scrollbar-track-indigo-200 overflow-x-scroll"
    )(img(cls := "rounded h-32 sm:h-56 w-auto m-1")),
    p(
      "Since the onset of covid-19 mutual aid efforts in the Rochester Area have " +
        "been on the rise. One project many have invested time into is the free " +
        "food stand project. Many different organizations and individuals have " +
        "taken part in this collective effort to combat food insecurity. This " +
        "website is meant as a tool for these organizations and individuals to" +
        "share information people who use these free resources. The map contains " +
        "locations and information about these locations as to how to use the aid" +
        "they contain. If you have a resource you would like to add to the map log " +
        "in or sign up and go to the map. This website does not take credit for or " +
        "manage any of these locations. If you would like to get in contact with a " +
        "location steward please see that locations page."
    )
  )
