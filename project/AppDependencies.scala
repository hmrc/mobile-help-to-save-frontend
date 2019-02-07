import play.core.PlayVersion
import play.sbt.PlayImport.ws
import sbt.{ModuleID, _}

object AppDependencies {

  lazy val appDependencies: Seq[ModuleID] =
    compile ++ test ++ integrationTest

  val compile = Seq(
    "uk.gov.hmrc" %% "govuk-template" % "5.27.0-play-26",
    "uk.gov.hmrc" %% "play-ui" % "7.31.0-play-26",
    ws,
    "uk.gov.hmrc" %% "bootstrap-play-26" % "0.36.0"
  )

  val test: Seq[ModuleID] = testCommon("test")

  val integrationTest: Seq[ModuleID] = testCommon("it") ++ Seq(
    "com.github.tomakehurst" % "wiremock" % "2.21.0" % "it",
    "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % "it",
    "com.typesafe.play" %% "play-test" % PlayVersion.current % "it"
  )

  def testCommon(scope: String) = Seq(
    "org.scalatest" %% "scalatest" % "3.0.5" % scope,
    "org.pegdown" % "pegdown" % "1.6.0" % scope
  )

  // Transitive dependencies in scalatest/scalatestplusplay drag in a newer version of jetty that is not
  // compatible with wiremock, so we need to pin the jetty stuff to the older version.
  // see https://groups.google.com/forum/#!topic/play-framework/HAIM1ukUCnI
  val jettyVersion = "9.2.13.v20150730"
  def overrides(): Set[ModuleID] =  Set(
    "org.eclipse.jetty" % "jetty-server" % jettyVersion,
    "org.eclipse.jetty" % "jetty-servlet" % jettyVersion,
    "org.eclipse.jetty" % "jetty-security" % jettyVersion,
    "org.eclipse.jetty" % "jetty-servlets" % jettyVersion,
    "org.eclipse.jetty" % "jetty-continuation" % jettyVersion,
    "org.eclipse.jetty" % "jetty-webapp" % jettyVersion,
    "org.eclipse.jetty" % "jetty-xml" % jettyVersion,
    "org.eclipse.jetty" % "jetty-client" % jettyVersion,
    "org.eclipse.jetty" % "jetty-http" % jettyVersion,
    "org.eclipse.jetty" % "jetty-io" % jettyVersion,
    "org.eclipse.jetty" % "jetty-util" % jettyVersion,
    "org.eclipse.jetty.websocket" % "websocket-api" % jettyVersion,
    "org.eclipse.jetty.websocket" % "websocket-common" % jettyVersion,
    "org.eclipse.jetty.websocket" % "websocket-client" % jettyVersion
  )

}
