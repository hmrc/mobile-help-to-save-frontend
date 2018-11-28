/*
 * Copyright 2018 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.mobilehelptosavefrontend

import org.scalatest.{Matchers, OptionValues, WordSpec}
import play.api.Application
import play.api.http.HttpConfiguration
import play.api.libs.ws.{WSCookie, WSResponse}
import play.api.mvc.Session
import play.api.test.{DefaultAwaitTimeout, FutureAwaits}
import uk.gov.hmrc.crypto.Crypted
import uk.gov.hmrc.http.SessionKeys
import uk.gov.hmrc.mobilehelptosavefrontend.stubs.AuthStub
import uk.gov.hmrc.mobilehelptosavefrontend.support.{OneServerPerSuiteWsClient, WireMockSupport}
import uk.gov.hmrc.play.bootstrap.filters.frontend.crypto.SessionCookieCrypto

class SsoWorkaroundISpec extends WordSpec with Matchers with OptionValues
  with FutureAwaits with DefaultAwaitTimeout
  with WireMockSupport with OneServerPerSuiteWsClient {

  private val configuredAccessAccountUrl = "http://example.com/test-help-to-save/access-account"
  override implicit lazy val app: Application = wireMockApplicationBuilder()
    .configure("helpToSave.accessAccountUrl" -> configuredAccessAccountUrl).build()

  private lazy val sessionCrypto = app.injector.instanceOf[SessionCookieCrypto]
  private lazy val httpConfiguration = app.injector.instanceOf[HttpConfiguration]


  "GET /mobile-help-to-save/access-account" should {
    behave like anSsoWorkaroundEndpoint(withUrl = "/mobile-help-to-save/access-account", redirectingToUrl = configuredAccessAccountUrl)
  }

  private def anSsoWorkaroundEndpoint(withUrl: String, redirectingToUrl: String): Unit = {
    "redirect and add affinityGroup to session cookie when user is logged in" in {
      AuthStub.userIsLoggedIn()
      val response = await(wsUrl(withUrl)
        .withFollowRedirects(false)
        .get())
      response.status shouldBe 303
      response.header("Location").value shouldBe redirectingToUrl
      playSession(response).get(SessionKeys.affinityGroup) shouldBe Some("Individual")
    }

    "redirect even when user is not logged in (affinityGroup workaround not required)" in {
      AuthStub.userIsNotLoggedIn()
      val response = await(wsUrl(withUrl)
        .withFollowRedirects(false)
        .get())
      response.status shouldBe 303
      response.header("Location").value shouldBe redirectingToUrl
    }
  }

  private def playSession(response: WSResponse): Map[String, String] = {
    def decryptSessionCookie(value: String): String = sessionCrypto.crypto.decrypt(Crypted(value)).value

    val maybeSessionCookie: Option[WSCookie] = response.cookie(httpConfiguration.session.cookieName)
    val maybeSessionData: Option[String] = maybeSessionCookie.flatMap(_.value.map(decryptSessionCookie))

    maybeSessionData.map(Session.decode).getOrElse(Map.empty)
  }

}
