import play.sbt.PlayImport.ws
import sbt.*

object AppDependencies {

  private val playBootstrap = "9.16.0"
  private val playFrontendVersion = "12.7.0"
  private val flexmarkVersion = "0.64.8"
  private val wireMockVersion = "2.21.0"

  lazy val appDependencies: Seq[ModuleID] =
    compile ++ test ++ integrationTest

  val compile = Seq(
    "uk.gov.hmrc" %% "play-frontend-hmrc-play-30" % playFrontendVersion,
    ws,
    "uk.gov.hmrc" %% "bootstrap-frontend-play-30" % playBootstrap
  )

  val test: Seq[ModuleID] = testCommon("test")

  val integrationTest: Seq[ModuleID] = testCommon("it") ++ Seq.empty

  def testCommon(scope: String) = Seq(
    "com.vladsch.flexmark" % "flexmark-all"           % flexmarkVersion % scope,
    "uk.gov.hmrc"         %% "bootstrap-test-play-30" % playBootstrap   % scope
  )

}
