/*
 * Copyright 2016-2020 47 Degrees, LLC. <http://www.47deg.com>
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

package github4s

import github4s.taglessFinal.modules.GHWorkflow
import scala.language.higherKinds

/**
 * Represent the Github API wrapper
 * @param accessToken to identify the authenticated user
 */
class Github[F[_]](accessToken: Option[String] = None)(implicit git: GHWorkflow[F]) {
  val users         = git.users
  val repos         = git.repos
  val auth          = git.auth
  val gists         = git.gists
  val issues        = git.issues
  val activities    = git.activities
  val gitData       = git.gitData
  val pullRequests  = git.pullRequests
  val organizations = git.organizations

}

/** Companion object for [[github4s.Github]] */
object Github {

  def apply[F[_]](accessToken: Option[String] = None)(implicit git: GHWorkflow[F]) =
    new Github[F](accessToken)

}
