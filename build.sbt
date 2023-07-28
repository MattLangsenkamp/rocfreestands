import org.scalajs.linker.interface.ModuleSplitStyle

val scala3Version = "3.3.0"

ThisBuild / tlBaseVersion := "0.1"
ThisBuild / scalaVersion := scala3Version

lazy val front = crossProject(JSPlatform)
  .crossType(CrossType.Full)
  .enablePlugins(ScalaJSPlugin) // Enable the Scala.js plugin in this project
  .settings(

    scalaJSUseMainModuleInitializer := true,
    scalaJSLinkerConfig ~= {
      _.withModuleKind(ModuleKind.ESModule)
        .withModuleSplitStyle(
          ModuleSplitStyle.SmallModulesFor(List("front", "core")))
    },
    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "2.4.0",
  ).dependsOn(core)

lazy val core = crossProject(JVMPlatform, JSPlatform)
  .crossType(CrossType.Full)
  .settings(
    libraryDependencies ++= Seq(
      "com.disneystreaming.smithy4s" %%% "smithy4s-core" % smithy4sVersion.value
    ),
    smithy4sAllowedNamespaces := List("hello"),
    Compile / smithy4sInputDirs := List(
      (ThisBuild / baseDirectory).value / "core" / "shared" / "src" / "main" / "smithy"
    ),
  )
  .enablePlugins(Smithy4sCodegenPlugin)

lazy val root = tlCrossRootProject.aggregate(front, core).settings(
  Compile / doc / sources := Seq()
)