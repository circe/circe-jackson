organization in ThisBuild := "io.circe"

val compilerOptions = Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-feature",
  "-language:existentials",
  "-language:higherKinds",
  "-unchecked",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Ywarn-unused-import",
  "-Xfuture"
)

val circeVersion = "0.10.1"
val previousCirceJacksonVersion = "0.9.0"

val baseSettings = Seq(
  scalacOptions ++= compilerOptions,
  scalacOptions in (Compile, console) ~= {
    _.filterNot(Set("-Ywarn-unused-import"))
  },
  scalacOptions in (Test, console) ~= {
    _.filterNot(Set("-Ywarn-unused-import"))
  },
  resolvers ++= Seq(
    Resolver.sonatypeRepo("releases"),
    Resolver.sonatypeRepo("snapshots")
  ),
  coverageHighlighting := true,
  coverageScalacPluginVersion := "1.3.0",
  (scalastyleSources in Compile) ++= (unmanagedSourceDirectories in Compile).value,
  libraryDependencies ++= Seq(
    "io.circe" %% "circe-core" % circeVersion,
    "io.circe" %% "circe-jawn" % circeVersion % Test,
    "io.circe" %% "circe-testing" % circeVersion % Test
  ),
  unmanagedSourceDirectories in Compile += (baseDirectory in ThisBuild).value / "shared/src/main",
  unmanagedSourceDirectories in Test += (baseDirectory in ThisBuild).value / "shared/src/test",
  unmanagedResourceDirectories in Test += (baseDirectory in ThisBuild).value / "shared/src/test/resources"
)

val docMappingsApiDir = settingKey[String]("Subdirectory in site target directory for API docs")

def jacksonDependencies(version: String, databindVersion: Option[String] = None) =
  Seq(
    "com.fasterxml.jackson.core" % "jackson-core" % version,
    "com.fasterxml.jackson.core" % "jackson-databind" % databindVersion.getOrElse(version)
  )

val allSettings = baseSettings ++ publishSettings

val root = project.in(file("."))
  .settings(allSettings ++ noPublishSettings)
  .aggregate(jackson25, jackson26, jackson27, jackson28, jackson29, benchmark)
  .dependsOn(jackson28)

lazy val jackson25 = project.in(file("25"))
  .settings(allSettings)
  .settings(
    moduleName := "circe-jackson25",
    libraryDependencies ++= jacksonDependencies("2.5.5"),
    mimaPreviousArtifacts := Set("io.circe" %% "circe-jackson25" % previousCirceJacksonVersion)
  )

lazy val jackson26 = project.in(file("26"))
  .settings(allSettings)
  .settings(
    moduleName := "circe-jackson26",
    libraryDependencies ++= jacksonDependencies("2.6.7", Some("2.6.7.1")),
    unmanagedSourceDirectories in Compile += (baseDirectory in ThisBuild).value / "27",
    mimaPreviousArtifacts := Set("io.circe" %% "circe-jackson26" % previousCirceJacksonVersion)
  )

lazy val jackson27 = project.in(file("27"))
  .settings(allSettings)
  .settings(
    moduleName := "circe-jackson27",
    libraryDependencies ++= jacksonDependencies("2.7.9", Some("2.7.9.4")),
    mimaPreviousArtifacts := Set("io.circe" %% "circe-jackson27" % previousCirceJacksonVersion)
  )

lazy val jackson28 = project.in(file("28"))
  .enablePlugins(GhpagesPlugin)
  .settings(allSettings)
  .settings(
    moduleName := "circe-jackson28",
    libraryDependencies ++= jacksonDependencies("2.8.11", Some("2.8.11.2")),
    docMappingsApiDir := "api",
    addMappingsToSiteDir(mappings in (Compile, packageDoc), docMappingsApiDir),
    ghpagesNoJekyll := true,
    scalacOptions in (Compile, doc) ++= Seq(
      "-groups",
      "-implicits",
      "-doc-source-url", scmInfo.value.get.browseUrl + "/tree/master€{FILE_PATH}.scala",
      "-sourcepath", baseDirectory.in(LocalRootProject).value.getAbsolutePath
    ),
    git.remoteRepo := "git@github.com:circe/circe-jackson.git",
    autoAPIMappings := true,
    apiURL := Some(url("https://circe.github.io/circe-jackson/api/")),
    mimaPreviousArtifacts := Set("io.circe" %% "circe-jackson28" % previousCirceJacksonVersion)
  )

lazy val jackson29 = project.in(file("29"))
  .settings(allSettings)
  .settings(
    moduleName := "circe-jackson29",
    libraryDependencies ++= jacksonDependencies("2.9.6"),
    unmanagedSourceDirectories in Compile += (baseDirectory in ThisBuild).value / "28",
    mimaPreviousArtifacts := Set("io.circe" %% "circe-jackson29" % previousCirceJacksonVersion)
  )

lazy val benchmark = project.in(file("benchmark"))
  .settings(noPublishSettings)
  .settings(
    crossScalaVersions := crossScalaVersions.value.init,
    libraryDependencies ++= Seq(
      "io.circe" %% "circe-core" % circeVersion,
      "io.circe" %% "circe-generic" % circeVersion,
      "io.circe" %% "circe-jawn" % circeVersion,
      "io.circe" %% "circe-parser" % circeVersion % Test,
      "org.scalatest" %% "scalatest" % "3.0.5" % Test,
      compilerPlugin("org.scalamacros" % "paradise" % "2.1.1" % Test cross CrossVersion.full)
    )
  )
  .enablePlugins(JmhPlugin)
  .dependsOn(jackson25, jackson26, jackson27, jackson28)

lazy val noPublishSettings = Seq(
  publish := {},
  publishLocal := {},
  publishArtifact := false
)

lazy val publishSettings = Seq(
  releaseCrossBuild := true,
  releasePublishArtifactsAction := PgpKeys.publishSigned.value,
  homepage := Some(url("https://github.com/circe/circe-jackson")),
  licenses := Seq("Apache 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
  publishMavenStyle := true,
  publishArtifact in Test := false,
  pomIncludeRepository := { _ => false },
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value)
      Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("releases"  at nexus + "service/local/staging/deploy/maven2")
  },
  scmInfo := Some(
    ScmInfo(
      url("https://github.com/circe/circe-jackson"),
      "scm:git:git@github.com:circe/circe-jackson.git"
    )
  ),
  developers := List(
    Developer(
      "travisbrown",
      "Travis Brown",
      "travisrobertbrown@gmail.com",
      url("https://twitter.com/travisbrown")
    )
  )
)

credentials ++= (
  for {
    username <- Option(System.getenv().get("SONATYPE_USERNAME"))
    password <- Option(System.getenv().get("SONATYPE_PASSWORD"))
  } yield Credentials(
    "Sonatype Nexus Repository Manager",
    "oss.sonatype.org",
    username,
    password
  )
).toSeq
