name         := "sbt-jackson-version-check"
organization := "nl.gn0s1s"
startYear    := Some(2024)
homepage     := Some(url("https://github.com/philippus/sbt-jackson-version-check"))
licenses += License.Apache2

developers := List(
  Developer(
    id = "philippus",
    name = "Philippus Baalman",
    email = "",
    url = url("https://github.com/philippus")
  )
)

enablePlugins(SbtPlugin)

scalaVersion := "2.12.20"
crossScalaVersions += "3.7.3"

pluginCrossBuild / sbtVersion := {
  scalaBinaryVersion.value match {
    case "2.12" => "1.10.5"
    case _      => "2.0.0-RC4"
  }
}

ThisBuild / versionScheme          := Some("semver-spec")
ThisBuild / versionPolicyIntention := Compatibility.BinaryCompatible

Compile / packageBin / packageOptions += Package.ManifestAttributes(
  "Automatic-Module-Name" -> "nl.gn0s1s.jackson.versioncheck"
)

scriptedLaunchOpts := {
  scriptedLaunchOpts.value ++ Seq("-Xmx1024M", "-Dplugin.version=" + version.value)
}

scriptedBufferLog := false
