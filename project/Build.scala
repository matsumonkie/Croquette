import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {
  val appName         = "Croquette"
  val appVersion      = "1.0-SNAPSHOT"
    val appDependencies = Seq(
	//for XMPP
    "org.igniterealtime.smack" % "smack" % "3.2.1",
	//to retrieve contact from google
	"com.google.gdata" % "core" % "1.47.1",
    //to capitalize name
    "commons-lang" % "commons-lang" % "2.5"
)
    
    val main = PlayProject(appName, appVersion, appDependencies, mainLang = JAVA).settings(
      // Add your own project settings here      
    )







}
