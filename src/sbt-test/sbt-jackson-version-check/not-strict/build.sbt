scalaVersion := "2.13.15"

// direct dependency mismatch
libraryDependencies ++= Seq(
  "com.fasterxml.jackson.core"       % "jackson-annotations"    % "2.18.1",
  "com.fasterxml.jackson.core"       % "jackson-core"           % "2.18.1",
  "com.fasterxml.jackson.core"       % "jackson-databind"       % "2.18.1",
  "com.fasterxml.jackson.dataformat" % "jackson-dataformat-csv" % "2.18.0"
)

jacksonVersionCheckFailBuildOnNonMatchingVersions := true
jacksonVersionCheckStrict                         := false

TaskKey[Unit]("check") := {
  val lastLog: File = BuiltinCommands.lastLogFile(state.value).get
  val last: String  = IO.read(lastLog)
  if (last.contains("You are using version 2.18.1 of Jackson, but "))
    sys.error("not expected mention of non-matching Jackson module versions")
}
