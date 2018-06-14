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

name := (name in ThisBuild).value

lazy val compilerPlugins = Seq(
  addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full),
  addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.2.4")
)

import sbtcrossproject.CrossPlugin.autoImport.{crossProject, CrossType}

lazy val server = project.in(file("server"))
  .settings(
    libraryDependencies ++= Seq(
      "ch.qos.logback" % "logback-classic" % "1.2.3",
      "org.http4s" %% "http4s-dsl" % "0.18.12",
      "org.http4s" %% "http4s-blaze-server" % "0.18.12",
      "io.monix" %%% "monix" % "3.0.0-RC1",
      "com.lihaoyi" %%% "scalatags" % "0.6.7"
    ),

    scalacOptions ++= Seq("-Ypartial-unification")
  )
