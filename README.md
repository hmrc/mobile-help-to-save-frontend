# mobile-help-to-save-frontend

[![Build Status](https://travis-ci.org/hmrc/mobile-help-to-save-frontend.svg)](https://travis-ci.org/hmrc/mobile-help-to-save-frontend) [ ![Download](https://api.bintray.com/packages/hmrc/releases/mobile-help-to-save-frontend/images/download.svg) ](https://bintray.com/hmrc/releases/mobile-help-to-save-frontend/_latestVersion)

Works around an incompatibility between the Web Session API and the MDTP->NS&I SSO mechanism by populating the affinityGroup session attribute.

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html")


## Run Tests
- Run Unit Tests:  `sbt test`
- Run Integration Tests: `sbt it:test`
- Run Unit and Integration Tests: `sbt test it:test`
- Run Unit and Integration Tests with coverage report: `sbt clean compile coverage test it:test coverageReport dependencyUpdates`
