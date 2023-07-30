package front.model

import tyrian.Attr
import tyrian.Html.style
import tyrian.Html.styles

object Styles:
  val myStyles: Attr[Nothing] =
    styles(
      "width"      -> "100%",
      "height"     -> "40px",
      "padding"    -> "10px 0",
      "font-size"  -> "2em",
      "text-align" -> "center"
    )

  val containerClasses: String =
    "h-screen flex  scrollbar-thin" +
      " overflow-y-scroll flex-col" +
      " justify-between scrollbar-thin " +
      "scrollbar-thumb-indigo-500 scrollbar-track-indigo-200 "

  val mapButtonClasses: String =
    """
      |p-2 bg-white border focus:border-2
      |rounded text-gray-500 focus:text-gray-900
      |border-indigo-500 ml-2 hover:bg-gray-100
      |focus:outline-none
      |""".stripMargin

  val locationSClasses: String =
    "p-2 pb-12 m-auto max-w-m md:max-w-3xl lg:max-w-5xl xl:max-w-7xl h-56 text-red-600"
    
  val inputFormClasses: String = 
    "p-2 sm:flex w-full focus:outline-none focus:border-indigo-300 focus:ring-1 focus:ring-indigo-300"

  val mapStyle: Attr[Nothing] =
    style("height", "80vh")

  val z1000Style: Attr[Nothing] =
    style("z-index", "1000")

  val vh65Style: Attr[Nothing] =
    style("height", "65vh")

  val vhStyle: Int => Attr[Nothing] = i =>
    style("max-height", s"${i}vh")