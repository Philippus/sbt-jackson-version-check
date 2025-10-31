scalaVersion := "2.13.16"

// direct dependency mismatch
libraryDependencies ++= Seq(
  "tools.jackson.core"            % "jackson-databind"     % "3.0.0",
  "com.fasterxml.jackson.core"    % "jackson-databind"     % "2.18.0",
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.18.0"
)

jacksonVersionCheckFailBuildOnNonMatchingVersions := true
jacksonVersionCheckStrict                         := true

TaskKey[Unit]("check") := {
  val lastLog: File = BuiltinCommands.lastLogFile(state.value).get
  val last: String  = IO.read(lastLog)
  if (last.contains("Jackson Scala module 2.18.0 requires Jackson Databind version >= 2.18.0 and < 2.19.0"))
    sys.error("not expected mention of non-matching Jackson Scala module versions")
}
