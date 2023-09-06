import org.scalajs.linker.interface.ModuleSplitStyle
import _root_.sbtdocker.{DockerPlugin => DockPlug}
val scala3Version = "3.3.0"

ThisBuild / tlBaseVersion := "0.1"
ThisBuild / scalaVersion  := scala3Version
ThisBuild / organization  := "rocfreestands"

lazy val front = crossProject(JSPlatform)
  .crossType(CrossType.Full)
  .enablePlugins(ScalaJSPlugin)
  .enablePlugins(ScalablyTypedConverterExternalNpmPlugin)
  .settings(
    scalaJSLinkerConfig ~= {
      _.withModuleKind(ModuleKind.ESModule)
        .withModuleSplitStyle(ModuleSplitStyle.FewestModules)
        .withSourceMap(false)
    },
    libraryDependencies ++= Seq(
      "io.indigoengine" %%% "tyrian-io"     % "0.7.1",
      "org.http4s"      %%% "http4s-dom"    % "0.2.9",
      "io.circe"        %%% "circe-generic" % "0.15.0-M1",
      "org.http4s"      %%% "http4s-circe"  % "0.23.19",
      "org.scalameta"   %%% "munit"         % "0.7.29" % Test
    ),
    jsEnv := new org.scalajs.jsenv.nodejs.NodeJSEnv(),
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
    smithy4sAllowedNamespaces := List("com.rocfreestands.core"),
    Compile / smithy4sInputDirs := List(
      (ThisBuild / baseDirectory).value / "core" / "shared" / "src" / "main" / "smithy" / "com" / "rocfreestands"
    )
  )
  .enablePlugins(Smithy4sCodegenPlugin)

lazy val server = crossProject(JVMPlatform)
  .crossType(CrossType.Full)
  .settings(
    libraryDependencies ++= Seq(
      "org.scalameta"                %% "munit"                   % "0.7.29" % Test,
      "com.disneystreaming.smithy4s" %% "smithy4s-http4s-swagger" % smithy4sVersion.value,
      "org.http4s"                   %% "http4s-ember-server"     % "0.23.18",
      "org.tpolecat"                 %% "skunk-core"              % "0.6.0",
      "com.github.geirolz"           %% "fly4s-core"              % "0.0.18",
      "org.postgresql"                % "postgresql"              % "42.6.0",
      "dev.profunktor"               %% "http4s-jwt-auth"         % "1.2.0",
      "com.github.jwt-scala"         %% "jwt-core"                % "9.3.0",
      "com.github.jwt-scala"         %% "jwt-circe"               % "9.3.0",
      "is.cir"                       %% "ciris"                   % "3.2.0"
    ),
    fork                  := true,
    assemblyMergeStrategy := (_ => MergeStrategy.rename),
    assemblyJarName       := "rocfreestands-fat.jar",
    docker / dockerfile := {
      val appDir: File = stage.value
      val targetDir    = "/app"
      println(appDir)
      new Dockerfile {
        from("ghcr.io/graalvm/graalvm-community:20-ol7")
        expose(8081)
        expose(5432)
        entryPoint(s"$targetDir/bin/${executableScriptName.value}")
        copy(appDir, targetDir, chown = "daemon:daemon")
      }
    },
    docker / imageNames := Seq(ImageName("rocfreestands-http4s:latest"))
  )
  .enablePlugins(JavaAppPackaging)
  .enablePlugins(DockPlug)
  .dependsOn(core)

lazy val rocfreestands = tlCrossRootProject
  .aggregate(server, front, core)
  .settings(
    Compile / doc / sources := Seq()
  )
