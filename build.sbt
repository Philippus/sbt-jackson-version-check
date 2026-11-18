name         := "sbt-jackson-version-check"
organization := "nl.gn0s1s"
startYear    := Some(2024)
homepage     := Some(uri("https://github.com/philippus/sbt-jackson-version-check"))
licenses += License.Apache2

developers := List(
  Developer(
    id = "philippus",
    name = "Philippus Baalman",
    email = "",
    url = uri("https://github.com/philippus")
  )
)

enablePlugins(SbtPlugin)

scalaVersion       := "2.12.21"
crossScalaVersions := Seq("2.12.21", "3.9.0")

scalacOptions ++= {
  scalaBinaryVersion.value match {
    case "2.12" => Seq("-Xsource:3", "-release:8")
    case _      => Nil
  }
}

pluginCrossBuild / sbtVersion := {
  scalaBinaryVersion.value match {
    case "2.12" => "1.10.5"
    case _      => "2.1.0"
  }
}

ThisBuild / versionScheme          := Some("semver-spec")
ThisBuild / versionPolicyIntention := Compatibility.BinaryCompatible

Compile / packageBin / packageOptions += Package.ManifestAttributes(
  "Automatic-Module-Name" -> "nl.gn0s1s.jackson.versioncheck"
)

scriptedSbt := (if (scalaBinaryVersion.value == "2.12") "1.13.1" else "2.1.0")

scriptedLaunchOpts := {
  scriptedLaunchOpts.value ++ Seq("-Xmx1024M", "-Dplugin.version=" + version.value)
}

scriptedBufferLog := false
