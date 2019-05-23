/*
 * Copyright 2019 HM Revenue & Customs
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

import javax.inject.{Inject, Named, Singleton}
import play.api.mvc._
import uk.gov.hmrc.auth.core.retrieve.Retrievals
import uk.gov.hmrc.auth.core.{AffinityGroup, AuthConnector, AuthorisedFunctions, NoActiveSession}
import uk.gov.hmrc.http.SessionKeys
import uk.gov.hmrc.play.bootstrap.controller.FrontendController

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SsoWorkaroundController @Inject()(
  override val authConnector:                             AuthConnector,
  @Named("helpToSave.accessAccountUrl") accessAccountUrl: String,
  @Named("helpToSave.accountPayInUrl") accountPayInUrl:   String,
  @Named("helpToSave.infoUrl") infoUrl:                   String,
  override val controllerComponents:                      MessagesControllerComponents
)(
  implicit ec: ExecutionContext
) extends FrontendController(controllerComponents)
    with AuthorisedFunctions {

  val accessAccount: Action[AnyContent] = ssoWorkaround(accessAccountUrl)
  val payIn:         Action[AnyContent] = ssoWorkaround(accountPayInUrl)
  val info:          Action[AnyContent] = ssoWorkaround(infoUrl)

  private def ssoWorkaround(redirectToUrl: String): Action[AnyContent] = Action.async { implicit request =>
    val redirect: Result = Redirect(redirectToUrl)

    authorised().retrieve(Retrievals.affinityGroup) {
      case Some(affinityGroup: AffinityGroup) =>
        Future successful redirect.addingToSession(SessionKeys.affinityGroup -> affinityGroup.toString)
      case None =>
        Future successful redirect.removingFromSession(SessionKeys.affinityGroup)
    } recover {
      case _: NoActiveSession =>
        redirect
    }
  }

}
