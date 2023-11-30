package com.rocfreestands.front.components

import com.rocfreestands.front.model.{Model, Msg, Routes}
import tyrian.Html
import tyrian.Html._

def aboutPage: Model => Html[Msg] = (model: Model) =>

  val images =
    model.locations.map(p =>
      img(cls := "rounded h-32 sm:h-56 w-auto m-1", src := p.location.image, onClick(Msg.NoOp))
    )

  div(
    cls := "font-mono  text-gray-500 map p-2 pb-12 m-auto max-w-m md:max-w-3xl lg:max-w-5xl xl:max-w-7xl h-full"
  )(
    div(
      cls := "flex p-4 mb-8 justify-center scrollbar-thin scrollbar-thumb-indigo-500 scrollbar-track-indigo-200 overflow-x-scroll"
    )(images: _*),
    p(
      """Around the Rochester NY area there is a network of food stands which anyone can drop off food to, and anyone can pick up food from. 
        |These stands are not maintained by any one individual or group, but rather are maintained by the communities in which they reside. 
        |This website exists for two reasons. The first is to help make it easy for those who want to help maintain these stands by donating food, 
        |cleaning the stands or making repairs to the stands. The second is to help those in need of food locate a stand near them. If you know of 
        |a stand in the Rochester area that is not currently on the map, please email rocfreestands@gmail.com with a picture of the stand, 
        |the name of the stand, a description containing any other information about the stand, such as what group maintains it or special 
        |instructions for using it, and the address at which it is located.""".stripMargin
    )
  )
