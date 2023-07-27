
import org.scalajs.linker.interface.ModuleSplitStyle


val scala3Version = "3.3.0"

ThisBuild / scalaVersion := scala3Version

lazy val front = project.in(file("front"))
  .enablePlugins(ScalaJSPlugin) // Enable the Scala.js plugin in this project
  .settings(
    scalaJSUseMainModuleInitializer := true,
    scalaJSLinkerConfig ~= {
      _.withModuleKind(ModuleKind.ESModule)
        .withModuleSplitStyle(
          ModuleSplitStyle.SmallModulesFor(List("front")))
    },
    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "2.4.0",
  )
  .dependsOn(shared)

lazy val shared = project.in(file("shared"))
  .enablePlugins(ScalaJSPlugin)
  .settings(
    scalaJSUseMainModuleInitializer := false
  )