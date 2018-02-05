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

import javax.inject.{Inject, Named, Singleton}

import play.api.http.HttpErrorHandler
import play.api.i18n.MessagesApi
import play.api.mvc.{Request, RequestHeader, Result, Results}
import play.twirl.api.Html
import uk.gov.hmrc.auth.core.NoActiveSession
import uk.gov.hmrc.mobilehelptosavefrontend.views
import uk.gov.hmrc.play.bootstrap.http.FrontendErrorHandler

import scala.concurrent.Future

trait NoActiveSessionErrorHandler extends HttpErrorHandler with Results {
}

@Singleton
class ErrorHandler @Inject()(
  val messagesApi: MessagesApi,
  implicit val appConfig: AppConfig,
  @Named("microservice.services.company-auth-frontend.external-url") companyAuthFrontendExternalUrl: String,
  @Named("microservice.services.company-auth-frontend.sign-in.path") companyAuthFrontendGgSignInPath: String
) extends FrontendErrorHandler with Results {
  override def standardErrorTemplate(pageTitle: String, heading: String, message: String)(implicit request: Request[_]): Html =
    views.html.error_template(pageTitle, heading, message)

  override def onServerError(request: RequestHeader, exception: Throwable): Future[Result] = exception match {
    case _: NoActiveSession =>
      Future successful Redirect(companyAuthFrontendExternalUrl + companyAuthFrontendGgSignInPath)
    case _ =>
      super.onServerError(request, exception)
  }
}
