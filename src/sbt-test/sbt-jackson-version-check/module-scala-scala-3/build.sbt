scalaVersion := "3.3.4"

// jackson-module-scala dependency mismatch
libraryDependencies ++= Seq(
  "com.fasterxml.jackson.core"    % "jackson-databind"     % "2.18.0",
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.14.2"
)

jacksonVersionCheckFailBuildOnNonMatchingVersions := true
jacksonVersionCheckStrict                         := true

TaskKey[Unit]("check") := {
  val lastLog: File = BuiltinCommands.lastLogFile(state.value).get
  val last: String  = IO.read(lastLog)
  if (!last.contains("Jackson Scala module 2.14.2 requires Jackson Databind version >= 2.14.0 and < 2.15.0"))
    sys.error("expected mention of non-matching Jackson Scala module versions")
}
