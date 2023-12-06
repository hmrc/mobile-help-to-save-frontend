import play.core.PlayVersion
import play.sbt.PlayImport.ws
import sbt.{ModuleID, _}

object AppDependencies {

  private val play28Bootstrap      = "8.1.0"
  private val playFrontendVersion  = "7.14.0-play-28"
  private val pegdownVersion       = "1.6.0"
  private val wireMockVersion      = "2.21.0"
  private val scalaTestPlusVersion = "4.0.3"

  lazy val appDependencies: Seq[ModuleID] =
    compile ++ test ++ integrationTest

  val compile = Seq(
    "uk.gov.hmrc" %% "play-frontend-hmrc" % playFrontendVersion,
    ws,
    "uk.gov.hmrc" %% "bootstrap-frontend-play-28" % play28Bootstrap
  )

  val test: Seq[ModuleID] = testCommon("test")

  val integrationTest: Seq[ModuleID] = testCommon("it") ++ Seq.empty

  def testCommon(scope: String) = Seq(
    "org.pegdown" % "pegdown"                 % pegdownVersion  % scope,
    "uk.gov.hmrc" %% "bootstrap-test-play-28" % play28Bootstrap % scope
  )

}
