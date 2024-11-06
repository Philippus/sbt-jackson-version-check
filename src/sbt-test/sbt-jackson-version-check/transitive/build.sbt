scalaVersion := "2.13.15"

// transitive dependency mismatch (elastic4s pulls in jackson 2.17.2 modules)
libraryDependencies ++= Seq(
  "com.fasterxml.jackson.core" % "jackson-annotations"     % "2.18.1",
  "com.fasterxml.jackson.core" % "jackson-core"            % "2.18.1",
  "com.fasterxml.jackson.core" % "jackson-databind"        % "2.18.1",
  "nl.gn0s1s"                 %% "elastic4s-client-esjava" % "8.14.0"
)

jacksonVersionCheckFailBuildOnNonMatchingVersions := true
jacksonVersionCheckStrict := true

TaskKey[Unit]("check") := {
  val lastLog: File = BuiltinCommands.lastLogFile(state.value).get
  val last: String  = IO.read(lastLog)
  if (!last.contains("You are using version 2.18.1 of Jackson, but "))
    sys.error("expected mention of non-matching Jackson module versions")
}
