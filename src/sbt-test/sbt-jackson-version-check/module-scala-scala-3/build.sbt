scalaVersion := "3.3.4"

// jackson-module-scala dependency mismatch
libraryDependencies ++= Seq(
  "com.fasterxml.jackson.core"    % "jackson-databind"     % "2.18.0",
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.14.2"
)

jacksonVersionCheckFailBuildOnNonMatchingVersions := true
jacksonVersionCheckStrict                         := true

@transient
lazy val check = taskKey[Unit]("Check the previous task's log")

check := {
  val taskStreams = (jacksonVersionCheck / streams).value
  val reader = taskStreams.readText(taskStreams.key)
  val last: String =
    try Iterator.continually(reader.readLine()).takeWhile(_ != null).mkString("\n")
    finally reader.close()
  if (!last.contains("Jackson Scala module 2.14.2 requires Jackson Databind version >= 2.14.0 and < 2.15.0"))
    sys.error("expected mention of non-matching Jackson Scala module versions")
}
