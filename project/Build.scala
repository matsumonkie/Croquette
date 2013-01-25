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
    "org.igniterealtime.smack" % "smack" % "3.2.1",
	"org.igniterealtime.smack" % "smackx" % "3.2.1",
	//to retrieve contact from google
	"com.google.gdata" % "core" % "1.47.1",
	//to capitalize name
	"commons-lang" % "commons-lang" % "2.5",
	// cache Google
	"com.google.guava" % "guava" % "14.0-rc1"/*,
	// JSON
	"com.google.code.gson" % "gson" % "2.2.2"*/
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here      
  )
}
