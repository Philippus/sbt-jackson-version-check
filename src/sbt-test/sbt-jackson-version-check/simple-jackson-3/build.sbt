scalaVersion := "2.13.16"

// direct dependency mismatch
libraryDependencies ++= Seq(
  "com.fasterxml.jackson.core"       % "jackson-annotations"    % "2.20",
  "tools.jackson.core"               % "jackson-core"           % "3.0.0",
  "tools.jackson.core"               % "jackson-databind"       % "3.0.0",
  "com.fasterxml.jackson.dataformat" % "jackson-dataformat-csv" % "2.16.1"
)

jacksonVersionCheckFailBuildOnNonMatchingVersions := true
jacksonVersionCheckStrict                         := true

TaskKey[Unit]("check") := {
  val lastLog: File = BuiltinCommands.lastLogFile(state.value).get
  val last: String  = IO.read(lastLog)
  if (!last.contains("You are using version 3.0.0 of Jackson, but "))
    sys.error("expected mention of non-matching Jackson module versions")
}
