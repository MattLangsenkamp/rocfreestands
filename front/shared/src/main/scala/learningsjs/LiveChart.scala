package learningsjs
import scala.scalajs.js
import scala.scalajs.js.annotation.*
import org.scalajs.dom
import hello.*
import smithy4s.Timestamp

// import javascriptLogo from "/javascript.svg"
@js.native @JSImport("/javascript.svg", JSImport.Default)
val javascriptLogo: String = js.native

@main
def LiveChart(): Unit =
  dom.document.querySelector("#app").innerHTML = s"""
    <div>
      <a href="https://vitejs.dev" target="_blank">
        <img src="/vite.svg" class="logo pl-7 text-red-600/75" alt="Vite logo" />
      </a>
      <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript" target="_blank">
        <img src="$javascriptLogo" class="logo vanilla" alt="JavaScript logo" />
      </a>
      <h1>Hello Scala.js!</h1>
      <div class="card">
        <button id="counter" type="button"></button>
      </div>
      <p class="read-the-docs">
        Click on the Vite logo to learn more
      </p>
    </div>
  """

  setupCounter2(dom.document.getElementById("counter"))
end LiveChart

def setupCounter2(element: dom.Element): Unit =
  //val m = Model(97, "hello")
  var counter = 0
  val L = Location(99, "", "", "", 0, 0, Timestamp.epoch)

  def setCounter(count: Int): Unit =
    counter = L.id // m.count
    element.innerHTML = s"count is $counter"

  element.addEventListener("click", e => setCounter(counter + 1))
  setCounter(0)
end setupCounter2