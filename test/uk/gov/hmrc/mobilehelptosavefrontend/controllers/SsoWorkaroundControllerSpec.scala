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

package uk.gov.hmrc.mobilehelptosavefrontend.controllers

import org.scalatest.{Matchers, WordSpec}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.mvc._
import play.api.test.{DefaultAwaitTimeout, FakeRequest, FutureAwaits}
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.{Retrieval, Retrievals}
import uk.gov.hmrc.auth.core.{AffinityGroup, AuthConnector}
import uk.gov.hmrc.http.{HeaderCarrier, SessionKeys}

import scala.concurrent.{ExecutionContext, Future}

class SsoWorkaroundControllerSpec extends WordSpec with Matchers with FutureAwaits with DefaultAwaitTimeout with GuiceOneAppPerSuite {

  private val configuredInvitationUrl = "/help-to-save"
  private val configuredAccessAccountUrl = "/help-to-save/access-account"

  "invitation" should {
    behave like anSsoWorkaroundEndpoint(_.invitation, configuredInvitationUrl)
  }

  "accessAccount" should {
    behave like anSsoWorkaroundEndpoint(_.accessAccount, configuredAccessAccountUrl)
  }

  private def anSsoWorkaroundEndpoint(getAction: SsoWorkaroundController => Action[AnyContent], redirectingToUrl: String): Unit = {

    "retrieve affinityGroup from auth and copy it into the Play session" in {
      val fakeAuthConnector = new AuthConnector {
        override def authorise[A](predicate: Predicate, retrieval: Retrieval[A])(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[A] = retrieval match {
          case Retrievals.affinityGroup => Future successful Some(AffinityGroup.Individual).asInstanceOf[A]
          case _ => ???
        }
      }
      val controller = new SsoWorkaroundController(fakeAuthConnector,
        invitationUrl = configuredInvitationUrl, accessAccountUrl = configuredAccessAccountUrl)
      val action = getAction(controller)

      implicit val request: Request[AnyContentAsEmpty.type] = FakeRequest()
      val result: Result = await(action(request))
      result.session.get(SessionKeys.affinityGroup) shouldBe Some("Individual")

      result.header.status shouldBe 303
      result.header.headers("Location") shouldBe redirectingToUrl
    }

    "clear affinityGroup in play session when affinityGroup retrieved from auth is None" in {
      val fakeAuthConnector = new AuthConnector {
        override def authorise[A](predicate: Predicate, retrieval: Retrieval[A])(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[A] = retrieval match {
          case Retrievals.affinityGroup => Future successful None.asInstanceOf[A]
          case _ => ???
        }
      }
      val controller = new SsoWorkaroundController(fakeAuthConnector,
        invitationUrl = configuredInvitationUrl, accessAccountUrl = configuredAccessAccountUrl)
      val action = getAction(controller)

      implicit val request: Request[AnyContentAsEmpty.type] = FakeRequest().withSession(SessionKeys.affinityGroup -> "OldAffinityGroupInSession")
      val result: Result = await(action(request))
      result.session.get(SessionKeys.affinityGroup) shouldBe None

      result.header.status shouldBe 303
      result.header.headers("Location") shouldBe redirectingToUrl
    }

  }
}
