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

package github4s.taglessFinal.modules

import cats.effect.ConcurrentEffect
import github4s.api._
import github4s.taglessFinal.algebra._
import github4s.taglessFinal.interpreters._
import github4s.GithubDefaultUrls._

sealed trait GHWorkflow[F[_]] {
  val users: UserAlg[F]
  val repos: RepositoryAlg[F]
  val auth: AuthAlg[F]
  val gists: GistAlg[F]
  val issues: IssuesAlg[F]
  val activities: ActivityAlg[F]
  val gitData: GitDataAlg[F]
  val pullRequests: PullRequestAlg[F]
  val organizations: OrganizationAlg[F]

}

class GithubAPI[F[_]: ConcurrentEffect](accessToken: Option[String] = None) extends GHWorkflow[F] {
  implicit val userHttp  = new Users[F]()
  implicit val repoHttp  = new Repos[F]()
  implicit val authHttp  = new Auth[F]()
  implicit val gistHttp  = new Gists[F]()
  implicit val issueHttp = new Issues[F]()
  implicit val actHttp   = new Activities[F]()
  implicit val dataHttp  = new GitData[F]()
  implicit val pullHttp  = new PullRequests[F]()
  implicit val orgHttp   = new Organizations[F]()

  override val users: UserAlg[F]                 = new UserInterpreter[F](accessToken)
  override val repos: RepositoryAlg[F]           = new RepositoryInterpreter[F](accessToken)
  override val auth: AuthAlg[F]                  = new AuthInterpreter[F](accessToken)
  override val gists: GistAlg[F]                 = new GistInterpreter[F](accessToken)
  override val issues: IssuesAlg[F]              = new IssueInterpreter[F](accessToken)
  override val activities: ActivityAlg[F]        = new ActivityInterpreter[F](accessToken)
  override val gitData: GitDataAlg[F]            = new GitDataInterpreter[F](accessToken)
  override val pullRequests: PullRequestAlg[F]   = new PullRequestInterpreter[F](accessToken)
  override val organizations: OrganizationAlg[F] = new OrganizationInterpreter[F](accessToken)
}
