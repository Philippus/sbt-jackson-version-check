scalaVersion := "2.13.15"

// transitive dependency mismatch (elastic4s pulls in jackson 2.17.2 modules)
libraryDependencies ++= Seq(
  "com.fasterxml.jackson.core" % "jackson-annotations"     % "2.18.1",
  "com.fasterxml.jackson.core" % "jackson-core"            % "2.18.1",
  "com.fasterxml.jackson.core" % "jackson-databind"        % "2.18.1",
  "nl.gn0s1s"                 %% "elastic4s-client-esjava" % "8.14.0"
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
  if (!last.contains("You are using version 2.18.1 of Jackson, but "))
    sys.error("expected mention of non-matching Jackson module versions")
}
