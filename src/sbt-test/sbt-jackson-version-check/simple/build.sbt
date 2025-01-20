scalaVersion := "2.13.15"

// direct dependency mismatch
libraryDependencies ++= Seq(
  "com.fasterxml.jackson.core"       % "jackson-annotations"    % "2.18.0",
  "com.fasterxml.jackson.core"       % "jackson-core"           % "2.18.0",
  "com.fasterxml.jackson.core"       % "jackson-databind"       % "2.18.0",
  "com.fasterxml.jackson.dataformat" % "jackson-dataformat-csv" % "2.16.1"
)

jacksonVersionCheckFailBuildOnNonMatchingVersions := true
jacksonVersionCheckStrict                         := true

TaskKey[Unit]("check") := {
  val lastLog: File = BuiltinCommands.lastLogFile(state.value).get
  val last: String  = IO.read(lastLog)
  if (!last.contains("You are using version 2.18.0 of Jackson, but "))
    sys.error("expected mention of non-matching Jackson module versions")
}
