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

import com.google.inject.AbstractModule
import com.google.inject.name.Names.named
import play.api.{Configuration, Environment}

class GuiceModule(environment: Environment, configuration: Configuration) extends AbstractModule {

  override def configure(): Unit = {
    bindConfigString("helpToSave.invitationUrl")
    bindConfigString("helpToSave.accessAccountUrl")
    bindConfigString("microservice.services.company-auth-frontend.external-url")
    bindConfigString("microservice.services.company-auth-frontend.sign-in.path")
  }

  private def bindConfigString(path: String): Unit = {
    bindConstant().annotatedWith(named(path)).to(configuration.underlying.getString(path))
  }

}
