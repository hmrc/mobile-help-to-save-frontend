import play.core.PlayVersion
import play.sbt.PlayImport.ws
import sbt.{ModuleID, _}

object AppDependencies {

  private val play26Bootstrap      = "1.3.0"
  private val govUkTemplateVersion = "5.48.0-play-26"
  private val playUiVersion        = "8.7.0-play-26"

  private val pegdownVersion       = "1.6.0"
  private val wireMockVersion      = "2.21.0"
  private val scalaTestVersion     = "3.0.5"
  private val scalaTestPlusVersion = "3.1.2"

  lazy val appDependencies: Seq[ModuleID] =
    compile ++ test ++ integrationTest

  val compile = Seq(
    "uk.gov.hmrc" %% "govuk-template" % govUkTemplateVersion,
    "uk.gov.hmrc" %% "play-ui"        % playUiVersion,
    ws,
    "uk.gov.hmrc" %% "bootstrap-play-26" % play26Bootstrap
  )

  val test: Seq[ModuleID] = testCommon("test")

  val integrationTest: Seq[ModuleID] = testCommon("it") ++ Seq(
      "com.github.tomakehurst" % "wiremock"            % wireMockVersion      % "it",
      "org.scalatestplus.play" %% "scalatestplus-play" % scalaTestPlusVersion % "it",
      "com.typesafe.play"      %% "play-test"          % PlayVersion.current  % "it"
    )

  def testCommon(scope: String) = Seq(
    "org.scalatest" %% "scalatest" % scalaTestVersion % scope,
    "org.pegdown"   % "pegdown"    % pegdownVersion   % scope
  )

  // Transitive dependencies in scalatest/scalatestplusplay drag in a newer version of jetty that is not
  // compatible with wiremock, so we need to pin the jetty stuff to the older version.
  // see https://groups.google.com/forum/#!topic/play-framework/HAIM1ukUCnI
  val jettyVersion = "9.2.13.v20150730"

  def overrides(): Seq[ModuleID] = Seq(
    "org.eclipse.jetty"           % "jetty-server"       % jettyVersion,
    "org.eclipse.jetty"           % "jetty-servlet"      % jettyVersion,
    "org.eclipse.jetty"           % "jetty-security"     % jettyVersion,
    "org.eclipse.jetty"           % "jetty-servlets"     % jettyVersion,
    "org.eclipse.jetty"           % "jetty-continuation" % jettyVersion,
    "org.eclipse.jetty"           % "jetty-webapp"       % jettyVersion,
    "org.eclipse.jetty"           % "jetty-xml"          % jettyVersion,
    "org.eclipse.jetty"           % "jetty-client"       % jettyVersion,
    "org.eclipse.jetty"           % "jetty-http"         % jettyVersion,
    "org.eclipse.jetty"           % "jetty-io"           % jettyVersion,
    "org.eclipse.jetty"           % "jetty-util"         % jettyVersion,
    "org.eclipse.jetty.websocket" % "websocket-api"      % jettyVersion,
    "org.eclipse.jetty.websocket" % "websocket-common"   % jettyVersion,
    "org.eclipse.jetty.websocket" % "websocket-client"   % jettyVersion
  )

}
