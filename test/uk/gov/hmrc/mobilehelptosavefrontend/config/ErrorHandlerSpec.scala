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

package uk.gov.hmrc.mobilehelptosavefrontend.config

import org.scalatest.{Matchers, WordSpec}
import org.scalatestplus.play.OneAppPerSuite
import play.api.http.{HeaderNames, Status}
import play.api.i18n.{DefaultLangs, DefaultMessagesApi}
import play.api.mvc.Results
import play.api.test.{DefaultAwaitTimeout, FakeRequest, ResultExtractors}
import play.api.{Configuration, Environment}
import uk.gov.hmrc.auth.core.NoActiveSession

class ErrorHandlerSpec extends WordSpec with Matchers
  with OneAppPerSuite
  with ResultExtractors with HeaderNames with Status with DefaultAwaitTimeout
  with Results {

  private val env = Environment.simple()
  private val configuration = Configuration.load(env)

  private val messageApi = new DefaultMessagesApi(env, configuration, new DefaultLangs(configuration))
  private val appConfig = new AppConfig(configuration, env)

  private val testErrorHandler = new ErrorHandler(
    messageApi, appConfig,
    companyAuthFrontendExternalUrl = "http://test",
    companyAuthFrontendGgSignInPath = "/test-gg-sign-in"
  )

  "onServerError" should {
    "redirect to Government Gateway sign in on NoActiveSession" in {
      val eventualResult = testErrorHandler.onServerError(FakeRequest(), new NoActiveSession("not logged in") {})

      status(eventualResult) shouldBe 303
      redirectLocation(eventualResult) shouldBe Some("http://test/test-gg-sign-in")
    }

    "call super on other errors" in {
      val eventualResult = testErrorHandler.onServerError(FakeRequest(), new RuntimeException)

      status(eventualResult) shouldBe 500
    }
  }
}
