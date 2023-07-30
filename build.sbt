import org.scalajs.linker.interface.ModuleSplitStyle

val scala3Version = "3.3.0"

ThisBuild / tlBaseVersion := "0.1"
ThisBuild / scalaVersion  := scala3Version

lazy val front = crossProject(JSPlatform)
  .crossType(CrossType.Full)
  .enablePlugins(ScalaJSPlugin)
  .enablePlugins(ScalablyTypedConverterExternalNpmPlugin)
  .settings(
    scalaJSLinkerConfig ~= {
      _.withModuleKind(ModuleKind.ESModule)
        .withModuleSplitStyle(ModuleSplitStyle.SmallModulesFor(List("front", "core")))
    },
    libraryDependencies ++= Seq(
      "io.indigoengine" %%% "tyrian-io"     % "0.7.1",
      "org.http4s"      %%% "http4s-dom"    % "0.2.9",
      "io.circe"        %%% "circe-generic" % "0.15.0-M1",
      "org.http4s"      %%% "http4s-circe"  % "0.23.19",
      "org.scalameta"   %%% "munit"         % "0.7.29" % Test
    ),
    externalNpm := {
      (ThisBuild / baseDirectory).value
    }
  )
  .dependsOn(core)

lazy val core = crossProject(JVMPlatform, JSPlatform)
  .crossType(CrossType.Full)
  .settings(
    libraryDependencies ++= Seq(
      "com.disneystreaming.smithy4s" %%% "smithy4s-core"   % smithy4sVersion.value,
      "com.disneystreaming.smithy4s" %%% "smithy4s-http4s" % smithy4sVersion.value
    ),
    smithy4sAllowedNamespaces := List("hello"),
    Compile / smithy4sInputDirs := List(
      (ThisBuild / baseDirectory).value / "core" / "shared" / "src" / "main" / "smithy"
    )
  )
  .enablePlugins(Smithy4sCodegenPlugin)

lazy val server = crossProject(JVMPlatform)
  .crossType(CrossType.Full)
  .settings(
    libraryDependencies ++= Seq(
      "org.scalameta"                %% "munit"                   % "0.7.29" % Test,
      "com.disneystreaming.smithy4s" %% "smithy4s-http4s-swagger" % smithy4sVersion.value,
      "org.http4s"                   %% "http4s-ember-server"     % "0.23.18"
    ),
    fork := true
  )
  .dependsOn(core)

lazy val rocfreestands = tlCrossRootProject
  .aggregate(server, front, core)
  .settings(
    Compile / doc / sources := Seq()
  )
