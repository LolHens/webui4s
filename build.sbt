inThisBuild(Seq(
  name := "webui4s",
  version := "0.0.0",

  scalaVersion := "2.12.6",

  resolvers ++= Seq(
    "lolhens-maven" at "http://artifactory.lolhens.de/artifactory/maven-public/",
    Resolver.url("lolhens-ivy", url("http://artifactory.lolhens.de/artifactory/ivy-public/"))(Resolver.ivyStylePatterns)
  ),

  scalacOptions ++= Seq("-Xmax-classfile-name", "127")
))

name := (ThisBuild / name).value

lazy val compilerPlugins = Seq(
  addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full),
  addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.2.4")
)

import sbtcrossproject.CrossPlugin.autoImport.{CrossType, crossProject}

lazy val shared = crossProject(JSPlatform, JVMPlatform).crossType(CrossType.Pure).in(file("shared"))
  .settings(compilerPlugins)
  .settings(
    libraryDependencies ++= Seq(
      "io.monix" %%% "monix" % "3.0.0-RC1",
      "com.lihaoyi" %%% "scalatags" % "0.6.7",
      "io.circe" %%% "circe-core" % "0.9.3",
      "io.circe" %%% "circe-generic" % "0.9.3",
      "io.circe" %%% "circe-parser" % "0.9.3"
    )
  )

lazy val sharedJs = shared.js
lazy val sharedJvm = shared.jvm

val publicResources = taskKey[Seq[File]]("Public resources")

def copyResourcesTask(resources: Seq[File], dir: String) =
  Def.task {
    val t = classDirectory.value / dir
    val s = streams.value
    val cacheStore = s.cacheStoreFactory make "copy-resources"
    import sbt.io.Path.flat
    val mappings = resources pair flat(t)
    s.log.debug("Copy resource mappings: " + mappings.mkString("\n\t", "\n\t", ""))
    Sync.sync(cacheStore)(mappings)
    mappings
  }

lazy val server = project.in(file("server"))
  .settings(
    libraryDependencies ++= Seq(
      "ch.qos.logback" % "logback-classic" % "1.2.3",
      "org.http4s" %% "http4s-dsl" % "0.18.12",
      "org.http4s" %% "http4s-blaze-server" % "0.18.12",
      "org.webjars" % "bootstrap" % "4.1.3"
    ),

    scalacOptions ++= Seq("-Ypartial-unification"),

    /*inConfig(Compile)(Seq(
      publicResources := Seq.empty,
      resources := resources.dependsOn(Def.taskDyn {
        copyResourcesTask(publicResources.value, "public")
      }).value
    )),*/

    //Compile / publicResources ++= (client / scalaJS).value,

    //watchSources ++= (client / watchSources).value,
  )
  .dependsOn(sharedJvm)
  .dependsOn(webjar(client))

val scalaJS = taskKey[Seq[File]]("ScalaJS output files")
val isDevMode = taskKey[Boolean]("Whether the app runs in development mode")

val packageWebjar = taskKey[File]("Produces a webjar.")


def webjar(project: Project): Project = project.settings(
  exportedProducts := Seq(Attributed.blank(packageWebjar.value))
)

lazy val client = project.in(file("client"))
  .enablePlugins(ScalaJSPlugin)
  .settings(
    scalaJSUseMainModuleInitializer := true,

    skip in packageJSDependencies := false,
    Compile / packageJSDependencies / crossTarget := (Compile / resourceManaged).value,

    fastOptJS / scalaJS := Seq(
      (Compile / fastOptJS).value.data,
      (Compile / fastOptJS).value.map(file => new File(file.toString + ".map")).data,
      (Compile / packageJSDependencies).value
    ),

    fullOptJS / scalaJS := Seq(
      (Compile / fullOptJS).value.data,
      (Compile / fullOptJS).value.map(file => new File(file.toString + ".map")).data,
      (Compile / packageMinifiedJSDependencies).value
    ),

    isDevMode := {
      val devCommands = Seq("run", "compile", "re-start", "reStart", "runAll")
      val executedCommandKey =
        state.value.history.currentOption
          .flatMap(_.commandLine.takeWhile(c => !c.isWhitespace).split(Array('/', ':')).lastOption)
          .getOrElse("")
      devCommands.contains(executedCommandKey)
    },

    scalaJS := Def.taskDyn {
      if (isDevMode.value)
        fastOptJS / scalaJS
      else
        fullOptJS / scalaJS
    }.value,

    packageWebjar / artifact := {
      val artifactValue = (packageWebjar / artifact).value
      artifactValue.withName(artifactValue.name + "-webjar")
    },
    Defaults.packageTaskSettings(packageWebjar, Def.task {
      def files = (fastOptJS / scalaJS).value

      def n = name.value

      def v = version.value

      files.map { file =>
        val fileName = file.name
        file -> s"META-INF/resources/webjars/$n/$v/js/$fileName"
      }
    })
  )
  .dependsOn(sharedJs)

