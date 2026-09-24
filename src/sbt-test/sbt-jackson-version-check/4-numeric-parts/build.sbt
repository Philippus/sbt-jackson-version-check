scalaVersion := "2.13.15"

// direct dependency mismatch
libraryDependencies ++= Seq(
  "com.fasterxml.jackson.core"    % "jackson-databind"     % "2.22.3",
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.22.3.1"
)

jacksonVersionCheckFailBuildOnNonMatchingVersions := false
jacksonVersionCheckStrict                         := true

TaskKey[Unit]("check") := {
  val lastLog: File = BuiltinCommands.lastLogFile(state.value).get
  val last: String  = IO.read(lastLog)
  if (last.contains("You are using version"))
    sys.error("expected no mention of non-matching Jackson module versions")
}
