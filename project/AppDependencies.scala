import play.core.PlayVersion
import play.sbt.PlayImport.ws
import sbt.{ModuleID, _}

object AppDependencies {

  lazy val appDependencies: Seq[ModuleID] = compile ++ test()

  val compile = Seq(
    "uk.gov.hmrc" %% "govuk-template" % "5.18.0",
    "uk.gov.hmrc" %% "play-ui" % "7.13.0",
    ws,
    "uk.gov.hmrc" %% "bootstrap-play-25" % "1.3.0"
  )

  def test(scope: String = "test") = Seq(
    "uk.gov.hmrc" %% "hmrctest" % "3.0.0" % scope,
    "org.scalatest" %% "scalatest" % "3.0.4" % scope,
    "org.scalatestplus.play" %% "scalatestplus-play" % "2.0.1" % scope,
    "org.pegdown" % "pegdown" % "1.6.0" % scope,
    "com.typesafe.play" %% "play-test" % PlayVersion.current % scope
  )

}
