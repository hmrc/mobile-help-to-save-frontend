/*
 * Copyright 2020 HM Revenue & Customs
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

import org.scalatest.OptionValues
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.Application
import play.api.http.HttpConfiguration
import play.api.test.{DefaultAwaitTimeout, FutureAwaits}
import uk.gov.hmrc.mobilehelptosavefrontend.stubs.AuthStub
import uk.gov.hmrc.mobilehelptosavefrontend.support.{OneServerPerSuiteWsClient, WireMockSupport}
import uk.gov.hmrc.play.bootstrap.frontend.filters.crypto.SessionCookieCrypto

class SsoWorkaroundISpec
    extends AnyWordSpec
    with Matchers
    with OptionValues
    with FutureAwaits
    with DefaultAwaitTimeout
    with WireMockSupport
    with OneServerPerSuiteWsClient {

  private val configuredAccessAccountUrl = "http://example.com/test-help-to-save/access-account"
  private val configuredAccountPayInUrl  = "http://example.com/test-help-to-save/pay-in"
  private val configuredInfoUrl          = "http://example.com/test-help-to-save/info"

  override implicit lazy val app: Application = wireMockApplicationBuilder()
    .configure("helpToSave.accessAccountUrl" -> configuredAccessAccountUrl)
    .configure("helpToSave.accountPayInUrl" -> configuredAccountPayInUrl)
    .configure("helpToSave.infoUrl" -> configuredInfoUrl)
    .build()

  private lazy val sessionCrypto     = app.injector.instanceOf[SessionCookieCrypto]
  private lazy val httpConfiguration = app.injector.instanceOf[HttpConfiguration]

  "GET /mobile-help-to-save/access-account" should {
    behave like anSsoWorkaroundEndpoint(withUrl          = "/mobile-help-to-save/access-account",
                                        redirectingToUrl = configuredAccessAccountUrl)
  }

  "GET /mobile-help-to-save/pay-in" should {
    behave like anSsoWorkaroundEndpoint(withUrl          = "/mobile-help-to-save/pay-in",
                                        redirectingToUrl = configuredAccountPayInUrl)
  }

  "GET /mobile-help-to-save/info" should {
    behave like anSsoWorkaroundEndpoint(withUrl = "/mobile-help-to-save/info", redirectingToUrl = configuredInfoUrl)
  }

  private def anSsoWorkaroundEndpoint(
    withUrl:          String,
    redirectingToUrl: String
  ): Unit = {
    "redirect and add affinityGroup to session cookie when user is logged in" in {
      AuthStub.userIsLoggedIn()
      val response = await(
        wsUrl(withUrl)
          .withFollowRedirects(false)
          .get()
      )
      response.status                   shouldBe 303
      response.header("Location").value shouldBe redirectingToUrl
    }

    "redirect even when user is not logged in (affinityGroup workaround not required)" in {
      AuthStub.userIsNotLoggedIn()
      val response = await(
        wsUrl(withUrl)
          .withFollowRedirects(false)
          .get()
      )
      response.status                   shouldBe 303
      response.header("Location").value shouldBe redirectingToUrl
    }
  }

}
