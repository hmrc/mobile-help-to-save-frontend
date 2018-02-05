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
import play.api.test.{DefaultAwaitTimeout, FutureAwaits}
import uk.gov.hmrc.mobilehelptosavefrontend.stubs.AuthStub
import uk.gov.hmrc.mobilehelptosavefrontend.support.{OneServerPerSuiteWsClient, WireMockSupport}

class SsoWorkaroundISpec extends WordSpec with Matchers with OptionValues
  with FutureAwaits with DefaultAwaitTimeout
  with WireMockSupport with OneServerPerSuiteWsClient {

  private val configuredInvitationUrl = "http://example.com/test-help-to-save/invitation"
  private val configuredAccessAccountUrl = "http://example.com/test-help-to-save/access-account"
  override implicit lazy val app: Application = wireMockApplicationBuilder()
    .configure(
      "helpToSave.invitationUrl" -> configuredInvitationUrl,
      "helpToSave.accessAccountUrl" -> configuredAccessAccountUrl,
      "microservice.services.company-auth-frontend.external-url" -> "http://localhost:9025",
      "microservice.services.company-auth-frontend.sign-in.path" -> "/gg/sign-in")
    .build()

  "GET /mobile-help-to-save" should {
    behave like anAuthenticatedRedirectEndpoint(withUrl = "/mobile-help-to-save", redirectingToUrl = configuredInvitationUrl)
  }

  "GET /mobile-help-to-save/access-account" should {
    behave like anAuthenticatedRedirectEndpoint(withUrl = "/mobile-help-to-save/access-account", redirectingToUrl = configuredAccessAccountUrl)
  }

  private def anAuthenticatedRedirectEndpoint(withUrl: String, redirectingToUrl: String): Unit = {
    "allow access if user is logged in" in {
      AuthStub.userIsLoggedIn()
      val response = await(wsUrl(withUrl)
        .withFollowRedirects(false)
        .get())
      response.status shouldBe 303
      response.header("Location").value shouldBe redirectingToUrl
    }

    "redirect to sign in if user is not logged in" in {
      AuthStub.userIsNotLoggedIn()
      val response = await(wsUrl(withUrl)
        .withFollowRedirects(false)
        .get())
      response.status shouldBe 303
      response.header("Location").value shouldBe "http://localhost:9025/gg/sign-in"
    }
  }

}
