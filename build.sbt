import com.typesafe.tools.mima.core._

ThisBuild / organization := "io.circe"
ThisBuild / crossScalaVersions := Seq("2.13.15", "2.12.20", "3.3.7")
ThisBuild / scalaVersion := crossScalaVersions.value.head
ThisBuild / startYear := Some(2016)
ThisBuild / tlBaseVersion := "0.14"
ThisBuild / scalafixAll / skip := tlIsScala3.value
ThisBuild / ScalafixConfig / skip := tlIsScala3.value
ThisBuild / tlCiScalafixCheck := false // TODO: Address these in a follow up PR
ThisBuild / tlFatalWarnings := false // TODO: fix by dropping 2.12
ThisBuild / tlCiReleaseTags := true
ThisBuild / tlCiReleaseBranches := Nil
ThisBuild / circeRootOfCodeCoverage := Some("root")

val circeVersion = "0.14.15"
val munitVersion = "1.2.2"
val munitScalacheckVersion = "1.2.0"

val previousCirceJacksonVersion = "0.15.0"
val disciplineMunitVersion = "2.0.0"

def priorTo2_13(scalaVersion: String): Boolean =
  CrossVersion.partialVersion(scalaVersion) match {
    case Some((2, minor)) if minor < 13 => true
    case _                              => false
  }

val baseSettings = Seq(
  resolvers ++= Resolver.sonatypeOssRepos("releases"),
  resolvers ++= Resolver.sonatypeOssRepos("snapshots"),
  libraryDependencies ++= Seq(
    "io.circe" %% "circe-core" % circeVersion,
    "org.scala-lang.modules" %% "scala-collection-compat" % "2.13.0",
    "io.circe" %% "circe-jawn" % circeVersion % Test,
    "io.circe" %% "circe-testing" % circeVersion % Test,
    "org.typelevel" %% "discipline-munit" % disciplineMunitVersion % Test,
    "org.scalameta" %% "munit" % munitVersion % Test,
    "org.scalameta" %% "munit-scalacheck" % munitScalacheckVersion % Test
  ),
  Compile / unmanagedSourceDirectories += (ThisBuild / baseDirectory).value / "shared/src/main",
  Test / unmanagedSourceDirectories += (ThisBuild / baseDirectory).value / "shared/src/test",
  Test / unmanagedResourceDirectories += (ThisBuild / baseDirectory).value / "shared/src/test/resources",
  Test / classLoaderLayeringStrategy := ClassLoaderLayeringStrategy.AllLibraryJars
)

val docMappingsApiDir = settingKey[String]("Subdirectory in site target directory for API docs")

def jacksonDependencies(version: String, databindVersion: Option[String] = None) =
  Seq(
    "com.fasterxml.jackson.core" % "jackson-core" % version,
    "com.fasterxml.jackson.core" % "jackson-databind" % databindVersion.getOrElse(version)
  )

val mimaSettings = Seq(
  mimaBinaryIssueFilters ++= Seq(
    ProblemFilters.exclude[MissingClassProblem]("io.circe.jackson.package$EnhancedByteArrayOutputStream")
  )
)

val allSettings = baseSettings ++ publishSettings ++ mimaSettings

val root = project
  .in(file("."))
  .settings(allSettings ++ noPublishSettings)
  .aggregate(
    jackson26,
    jackson26,
    jackson27,
    jackson28,
    jackson29,
    jackson210,
    jackson211,
    jackson212,
    jackson213,
    jackson214,
    jackson215,
    jackson216,
    jackson217,
    benchmark
  )
  .dependsOn(jackson210)

lazy val jackson25 = project
  .in(file("25"))
  .settings(allSettings)
  .settings(
    moduleName := "circe-jackson25",
    libraryDependencies ++= jacksonDependencies("2.5.5")
  )

lazy val jackson26 = project
  .in(file("26"))
  .settings(allSettings)
  .settings(
    moduleName := "circe-jackson26",
    libraryDependencies ++= jacksonDependencies("2.6.7", Some("2.6.7.5")),
    Compile / unmanagedSourceDirectories += (ThisBuild / baseDirectory).value / "27"
  )

lazy val jackson27 = project
  .in(file("27"))
  .settings(allSettings)
  .settings(
    moduleName := "circe-jackson27",
    libraryDependencies ++= jacksonDependencies("2.7.9", Some("2.7.9.7"))
  )

lazy val jackson28 = project
  .in(file("28"))
  .settings(allSettings)
  .settings(
    moduleName := "circe-jackson28",
    libraryDependencies ++= jacksonDependencies("2.8.11", Some("2.8.11.6")),
    Compile / doc / scalacOptions ++= Seq(
      "-groups",
      "-implicits",
      "-doc-source-url",
      scmInfo.value.get.browseUrl + "/tree/master€{FILE_PATH}.scala",
      "-sourcepath",
      (LocalRootProject / baseDirectory).value.getAbsolutePath
    ),
    autoAPIMappings := true,
    apiURL := Some(url("https://circe.github.io/circe-jackson/api/"))
  )

lazy val jackson29 = project
  .in(file("29"))
  .settings(allSettings)
  .settings(
    moduleName := "circe-jackson29",
    libraryDependencies ++= jacksonDependencies("2.9.10", Some("2.9.10.8")),
    Compile / unmanagedSourceDirectories += (ThisBuild / baseDirectory).value / "28"
  )

lazy val jackson210 = project
  .in(file("210"))
  .settings(allSettings)
  .settings(
    moduleName := "circe-jackson210",
    libraryDependencies ++= jacksonDependencies("2.10.5"),
    Compile / unmanagedSourceDirectories += (ThisBuild / baseDirectory).value / "210"
  )

lazy val jackson211 = project
  .in(file("211"))
  .settings(allSettings)
  .settings(
    moduleName := "circe-jackson211",
    libraryDependencies ++= jacksonDependencies("2.11.4"),
    Compile / unmanagedSourceDirectories += (ThisBuild / baseDirectory).value / "210"
  )

lazy val jackson212 = project
  .in(file("212"))
  .settings(allSettings)
  .settings(
    moduleName := "circe-jackson212",
    libraryDependencies ++= jacksonDependencies("2.12.7"),
    Compile / unmanagedSourceDirectories += (ThisBuild / baseDirectory).value / "210"
  )

lazy val jackson213 = project
  .in(file("213"))
  .settings(allSettings)
  .settings(
    moduleName := "circe-jackson213",
    libraryDependencies ++= jacksonDependencies("2.13.5"),
    Compile / unmanagedSourceDirectories += (ThisBuild / baseDirectory).value / "210",
    tlVersionIntroduced := List("2.12", "2.13", "3").map(k => k -> "0.14.2").toMap
  )

lazy val jackson214 = project
  .in(file("214"))
  .settings(allSettings)
  .settings(
    moduleName := "circe-jackson214",
    libraryDependencies ++= jacksonDependencies("2.14.3"),
    Compile / unmanagedSourceDirectories += (ThisBuild / baseDirectory).value / "210",
    tlVersionIntroduced := List("2.12", "2.13", "3").map(k => k -> "0.14.1").toMap
  )
lazy val jackson215 = project
  .in(file("215"))
  .settings(allSettings)
  .settings(
    moduleName := "circe-jackson215",
    libraryDependencies ++= jacksonDependencies("2.15.4"),
    Compile / unmanagedSourceDirectories += (ThisBuild / baseDirectory).value / "210",
    tlVersionIntroduced := List("2.12", "2.13", "3").map(k => k -> "0.14.1").toMap
  )

lazy val jackson216 = project
  .in(file("216"))
  .settings(allSettings)
  .settings(
    moduleName := "circe-jackson216",
    libraryDependencies ++= jacksonDependencies("2.16.2"),
    Compile / unmanagedSourceDirectories += (ThisBuild / baseDirectory).value / "210",
    tlVersionIntroduced := List("2.12", "2.13", "3").map(k => k -> "0.14.1").toMap
  )

lazy val jackson217 = project
  .in(file("217"))
  .settings(allSettings)
  .settings(
    moduleName := "circe-jackson217",
    libraryDependencies ++= jacksonDependencies("2.20.2"),
    Compile / unmanagedSourceDirectories += (ThisBuild / baseDirectory).value / "210",
    tlVersionIntroduced := List("2.12", "2.13", "3").map(k => k -> "0.14.1").toMap
  )

lazy val benchmark = project
  .in(file("benchmark"))
  .settings(allSettings)
  .settings(noPublishSettings)
  .settings(
    libraryDependencies ++= Seq(
      "io.circe" %% "circe-core" % circeVersion,
      "io.circe" %% "circe-generic" % circeVersion,
      "io.circe" %% "circe-jawn" % circeVersion,
      "io.circe" %% "circe-parser" % circeVersion % Test
    )
  )
  .enablePlugins(JmhPlugin)
  .dependsOn(jackson217)

lazy val noPublishSettings = Seq(
  publish := {},
  publishLocal := {},
  publishArtifact := false
)

lazy val publishSettings = Seq(
  homepage := Some(url("https://github.com/circe/circe-jackson")),
  publishMavenStyle := true,
  Test / publishArtifact := false,
  pomIncludeRepository := { _ => false },
  developers := List(
    tlGitHubDev("travisbrown", "Travis Brown"),
    tlGitHubDev("zathross", "Darren Gibson"),
    tlGitHubDev("hamnis", "Erlend Hamnaberg")
  )
)
