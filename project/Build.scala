import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "Croquette"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
	//groupid % artifact % version
    javaCore,
    javaJdbc,
    javaEbean,
	//for XMPP
    "jivesoftware" % "smack" % "3.0.4",
	//to retrieve contact from google
	"com.google.gdata" % "core" % "1.47.1"
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here      
  )

}
