scalaVersion := "2.13.15"

// direct dependency mismatch
libraryDependencies ++= Seq(
  "com.fasterxml.jackson.core"    % "jackson-databind"     % "2.22.3",
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.22.3.1"
)

jacksonVersionCheckFailBuildOnNonMatchingVersions := false
jacksonVersionCheckStrict                         := true


@transient
lazy val check = taskKey[Unit]("Check the previous task's log")

check := {
  val taskStreams = (jacksonVersionCheck / streams).value
  val reader = taskStreams.readText(taskStreams.key)
  val last: String =
    try Iterator.continually(reader.readLine()).takeWhile(_ != null).mkString("\n")
    finally reader.close()
  if (last.contains("You are using version"))
    sys.error("expected no mention of non-matching Jackson module versions")
}
