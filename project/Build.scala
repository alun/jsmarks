import sbt._ 
import Keys._ 
import com.github.siasia.WebPlugin
import WebPlugin._

object BuildSettings { 
  val buildOrganization = "com.katlex" 
  val buildScalaVersion = "2.9.1" 
  val buildVersion      = "0.1.0-SNAPSHOT" 
  val buildSettings = Defaults.defaultSettings ++ Seq(
        organization := buildOrganization,
        scalaVersion := buildScalaVersion,
        version      := buildVersion
  ) 
} 

object Dependencies { 

  val jetty = "org.mortbay.jetty" % "jetty" % "6.1.22" % "jetty,compile"
  val servletApi = "javax.servlet" % "servlet-api" % "2.5" % "provided->default"
  val logback = "ch.qos.logback" % "logback-classic" % "0.9.26" % "compile->default"

  val liftVersion = "2.4-M4"

  val lift_webkit = "net.liftweb" %% "lift-webkit" % liftVersion % "compile->default"
  val lift_textile = "net.liftweb" %% "lift-textile" % liftVersion % "compile->default"
  val lift_mongo_record = "net.liftweb" %% "lift-mongodb-record" % liftVersion % "compile->default"
  var yuicomp = "com.yahoo.platform.yui" % "yuicompressor" % "2.3.6"

  val liftDeps = Seq(jetty, servletApi, logback, lift_webkit, lift_mongo_record, lift_textile)

  val allDeps = liftDeps ++ Seq(yuicomp)
} 

object JSMarkProjectBuild extends Build { 
  import Dependencies._ 
  import BuildSettings._

  lazy val useWebappAsResource = resourceGenerators in Compile <+=
    (resourceManaged, baseDirectory) map { (managedBase, base) =>
      val webappBase = base / "src" / "main" / "webapp"
      for {
        (from, to) <- webappBase ** "*" x rebase(webappBase, managedBase / "main" / "webapp")
      } yield {
        Sync.copy(from, to)
        to
      }
    }

  lazy val core = Project("JS Mark lift site", file ("lift-site"),
    settings = buildSettings ++ WebPlugin.webSettings ++ Seq(
      useWebappAsResource,
      libraryDependencies := allDeps,
      jettyScanDirs := Nil,
      publishTo := Some(Resolver.ssh("katlex-repo", "katlex.com", 1022, "katlex/maven2") 
        withPermissions("0644") as ("alun", Path(Path.userHome) / ".ssh/id_rsa"))
    )
  )
}
