package nl.gn0s1s.jackson.versioncheck

import sbt.*
import sbt.Keys.*

object JacksonVersionCheckPlugin extends AutoPlugin {
  override def trigger = allRequirements

  object autoImport {
    lazy val jacksonVersionCheckFailBuildOnNonMatchingVersions =
      settingKey[Boolean]("Sets whether non-matching Jackson module versions fail the build")
    lazy val jacksonVersionCheckStrict                         =
      settingKey[Boolean]("Sets whether Jackson modules versions should match exactly, including the patch version.")
    val jacksonVersionCheck                                    =
      taskKey[Unit]("Check that all Jackson modules have the same version")
  }

  import autoImport.*

  override lazy val globalSettings: Seq[Def.Setting[Boolean]] = Seq(
    jacksonVersionCheckFailBuildOnNonMatchingVersions := false,
    jacksonVersionCheckStrict                         := false
  )

  override lazy val projectSettings: Seq[Def.Setting[Task[Unit]]] = {
    import nl.gn0s1s.jackson.versioncheck.Compat._

    Seq(
      jacksonVersionCheck := Def.uncached {
        checkModuleVersions(
          updateFull.value,
          streams.value.log,
          jacksonVersionCheckFailBuildOnNonMatchingVersions.value,
          jacksonVersionCheckStrict.value
        )
      }
    )
  }

  private val jacksonModules = Set(
    "jackson-core",
    "jackson-databind",
    "jackson-dataformat-avro",
    "jackson-dataformat-cbor",
    "jackson-dataformat-csv",
    "jackson-dataformat-ion",
    "jackson-dataformat-properties",
    "jackson-dataformat-protobuf",
    "jackson-dataformat-smile",
    "jackson-dataformat-toml",
    "jackson-dataformat-xml",
    "jackson-dataformat-yaml",
    "jackson-dataformats-binary",
    "jackson-dataformats-text",
    "jackson-datatype-eclipse-collections",
    "jackson-datatype-guava",
    "jackson-datatype-hibernate",
    "jackson-datatype-hibernate-parent",
    "jackson-datatype-hibernate3",
    "jackson-datatype-hibernate4",
    "jackson-datatype-hibernate5",
    "jackson-datatype-hibernate5-jakarta",
    "jackson-datatype-hibernate6",
    "jackson-datatype-hibernate7",
    "jackson-datatype-hppc",
    "jackson-datatype-jakarta-jsonp",
    "jackson-datatype-jakarta-mail",
    "jackson-datatype-javax-money",
    "jackson-datatype-jaxrs",
    "jackson-datatype-jdk7",
    "jackson-datatype-jdk8",
    "jackson-datatype-joda",
    "jackson-datatype-joda-money",
    "jackson-datatype-json-org",
    "jackson-datatype-jsr310",
    "jackson-datatype-jsr353",
    "jackson-datatype-pcollections",
    "jackson-datatypes-collections",
    "jackson-datatypes-misc-parent",
    "jackson-module-afterburner",
    "jackson-module-android-record",
    "jackson-module-blackbird",
    "jackson-module-guice",
    "jackson-module-guice7",
    "jackson-module-jakarta-xmlbind-annotations",
    "jackson-module-jaxb-annotations",
    "jackson-module-jsonSchema",
    "jackson-module-jsonSchema-jakarta",
    "jackson-module-jsonSchema-parent",
    "jackson-module-kotlin",
    "jackson-module-mrbean",
    "jackson-module-no-ctor-deser",
    "jackson-module-osgi",
    "jackson-module-parameter-names",
    "jackson-module-paranamer",
    "jackson-module-scala",
    "jackson-modules-base",
    "jackson-modules-java8"
  )

  private sealed trait Group

  private case object JacksonModule extends Group

  private case object Others extends Group

  private def moduleNameWithoutScalaVersion(m: ModuleID): String =
    m.name.replaceFirst("(_2\\.\\d\\d|_3)$", "")

  private def checkModuleVersions(
      updateReport: UpdateReport,
      log: Logger,
      failBuildOnNonMatchingVersions: Boolean,
      strict: Boolean
  ): Unit = {
    log.info("Checking Jackson module versions")
    val allModules      = updateReport.allModules
    val groupedJackson2 = allModules
      .filter(m =>
        m.organization == "com.fasterxml.jackson.core" || m.organization == "com.fasterxml.jackson.dataformat" ||
          m.organization == "com.fasterxml.jackson.datatype" || m.organization == "com.fasterxml.jackson.module"
      )
      .groupBy { m =>
        if (jacksonModules(moduleNameWithoutScalaVersion(m))) JacksonModule
        else Others
      }
    val jackson2Ok      =
      groupedJackson2.get(JacksonModule).forall(verifyVersions(
        _,
        log,
        failBuildOnNonMatchingVersions,
        strict
      ))

    val groupedJackson3 = allModules
      .filter(m =>
        m.organization == "tools.jackson.core" || m.organization == "tools.jackson.dataformat" ||
          m.organization == "tools.jackson.datatype" || m.organization == "tools.jackson.module"
      )
      .groupBy { m =>
        if (jacksonModules(moduleNameWithoutScalaVersion(m))) JacksonModule
        else Others
      }
    val jackson3Ok      =
      groupedJackson3.get(JacksonModule).forall(verifyVersions(
        _,
        log,
        failBuildOnNonMatchingVersions,
        strict
      ))

    for {
      jacksonDatabindVersion    <-
        allModules.filter(m => m.name == "jackson-databind" && m.organization == "com.fasterxml.jackson.core").map(m =>
          Version(m.revision)
        ).sorted.lastOption
      jacksonModuleScalaVersion <-
        allModules.filter(m =>
          moduleNameWithoutScalaVersion(m) == "jackson-module-scala" && m.organization == "com.fasterxml.jackson.module"
        ).map(m => Version(m.revision))
          .sorted.lastOption
    } yield verifyJacksonModuleScalaRequirement(jacksonDatabindVersion, jacksonModuleScalaVersion, log)

    for {
      jacksonDatabindVersion    <-
        allModules.filter(m => m.name == "jackson-databind" && m.organization == "tools.jackson.core").map(m =>
          Version(m.revision)
        ).sorted.lastOption
      jacksonModuleScalaVersion <-
        allModules.filter(m =>
          moduleNameWithoutScalaVersion(m) == "jackson-module-scala" && m.organization == "tools.jackson.module"
        ).map(m => Version(m.revision))
          .sorted.lastOption
    } yield verifyJacksonModuleScalaRequirement(jacksonDatabindVersion, jacksonModuleScalaVersion, log)

    if (failBuildOnNonMatchingVersions && !(jackson2Ok && jackson3Ok))
      throw NonMatchingVersionsException
  }

  private def extractMajorMinor(version: String): (Int, Int) =
    version.split('.') match {
      case Array(major, minor, _*) => (major.toInt, minor.toInt)
      case _                       => (0, 0)
    }

  private def verifyVersions(
      modules: Seq[ModuleID],
      log: Logger,
      failBuildOnNonMatchingVersions: Boolean,
      strict: Boolean
  ): Boolean = {
    val modulesLatestRevision = modules.maxBy(m => Version(m.revision)).revision
    val modulesTobeUpdated    =
      modules.collect {
        case m
            if (strict && m.revision != modulesLatestRevision) ||
              (!strict && extractMajorMinor(
                m.revision
              ) != extractMajorMinor(modulesLatestRevision)) => moduleNameWithoutScalaVersion(m)
      }.sorted
    if (modulesTobeUpdated.nonEmpty) {
      val groupedByVersion = modules
        .groupBy(_.revision)
        .toSeq
        .sortBy(r => Version(r._1))
        .map { case (k, v) => k -> v.map(moduleNameWithoutScalaVersion).sorted.mkString("[", ", ", "]") }
        .map { case (k, v) => s"($k, $v)" }
        .mkString(", ")
      val report           = s"You are using version $modulesLatestRevision of Jackson, but it appears " +
        s"you (perhaps indirectly) also depend on older versions of related modules. " +
        s"You can solve this by adding an explicit dependency on version $modulesLatestRevision " +
        s"of the [${modulesTobeUpdated.mkString(", ")}] modules to your project. " +
        s"Here's a complete collection of detected modules: $groupedByVersion."

      if (failBuildOnNonMatchingVersions)
        log.error(report)
      else
        log.warn(report)
    }
    modulesTobeUpdated.isEmpty
  }

  private def verifyJacksonModuleScalaRequirement(
      jacksonDatabindVersion: Version,
      jacksonModuleScalaVersion: Version,
      log: Logger
  ): Unit = {
    val (databindMajor, databindMinor)       = extractMajorMinor(jacksonDatabindVersion.version)
    val (moduleScalaMajor, moduleScalaMinor) = extractMajorMinor(jacksonModuleScalaVersion.version)

    if (databindMajor != moduleScalaMajor || databindMinor != moduleScalaMinor)
      log.warn(
        s"Jackson Scala module ${jacksonModuleScalaVersion.version} requires Jackson Databind version >=" +
          s" $moduleScalaMajor.$moduleScalaMinor.0 and < $moduleScalaMajor.${moduleScalaMinor + 1}.0 - Found " +
          s"jackson-databind version ${jacksonDatabindVersion.version}"
      )
  }
}
