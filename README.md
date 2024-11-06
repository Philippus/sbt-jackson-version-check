# sbt-jackson-version-check

[![build](https://github.com/Philippus/sbt-jackson-version-check/workflows/build/badge.svg)](https://github.com/Philippus/sbt-jackson-version-check/actions/workflows/scala.yml?query=workflow%3Abuild+branch%3Amain)
![Current Version](https://img.shields.io/badge/version-0.0.1-brightgreen.svg?style=flat "0.0.1")
[![Scala Steward badge](https://img.shields.io/badge/Scala_Steward-helping-blue.svg?style=flat&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA4AAAAQCAMAAAARSr4IAAAAVFBMVEUAAACHjojlOy5NWlrKzcYRKjGFjIbp293YycuLa3pYY2LSqql4f3pCUFTgSjNodYRmcXUsPD/NTTbjRS+2jomhgnzNc223cGvZS0HaSD0XLjbaSjElhIr+AAAAAXRSTlMAQObYZgAAAHlJREFUCNdNyosOwyAIhWHAQS1Vt7a77/3fcxxdmv0xwmckutAR1nkm4ggbyEcg/wWmlGLDAA3oL50xi6fk5ffZ3E2E3QfZDCcCN2YtbEWZt+Drc6u6rlqv7Uk0LdKqqr5rk2UCRXOk0vmQKGfc94nOJyQjouF9H/wCc9gECEYfONoAAAAASUVORK5CYII=)](https://scala-steward.org)
[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg?style=flat "Apache 2.0")](LICENSE)

This plugin can verify that the Jackson module versions of a project match. This check can run before running any
(integration) tests or running your application in production, thereby avoiding runtime surprises. You can use it also
as part of your build chain and make the build fail if non-matching versions are found.

According to the Jackson documentation extension modules are ONLY guaranteed to work with core components that have the
same minor version, see https://github.com/FasterXML/jackson/wiki/Jackson-Releases#internal-api-versioning.
Jackson-module-scala will always throw an exception if the major and minor versions do not match with jackson-databind's
versions, see https://github.com/FasterXML/jackson-module-scala/blob/9735bcb5b0abd17453fa4aa9f1eef5889467fbf7/src/main/scala/com/fasterxml/jackson/module/scala/JacksonModule.scala#L61.

## Installation

sbt-jackson-version-check is published for sbt 1.10.5 and above. To start using it add the following to your
`plugins.sbt`:

```
addSbtPlugin("nl.gn0s1s" % "sbt-jackson-version-check" % "0.0.1")
```

## Usage
### Tasks

| Task                | Description          | Command                         |
|:--------------------|:---------------------|:--------------------------------|
| jacksonVersionCheck | Runs version check.  | ```$ sbt jacksonVersionCheck``` |

### Configuration
You can configure the configuration in your `build.sbt` file.

| Setting                                           | Description                                                                                                                                           | Default Value |
|:--------------------------------------------------|:------------------------------------------------------------------------------------------------------------------------------------------------------|:--------------|
| jacksonVersionCheckFailBuildOnNonMatchingVersions | Sets whether non-matching versions fail the build, if `false` non-matching versions show up as warnings in the log, if `true` they show up as errors. | false         |
| jacksonVersionCheckStrict                         | Sets whether Jackson modules versions should match exactly, including the patch version. Otherwise only the major and minor versions should match.    | false         |   

## License
The code is available under the [Apache 2.0 License](LICENSE).
