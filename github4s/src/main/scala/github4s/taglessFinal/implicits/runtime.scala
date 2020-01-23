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

package github4s.taglessFinal.implicits

import cats.Applicative
import github4s.taglessFinal.algebra._
import github4s.taglessFinal.interpreters._
import github4s.taglessFinal.modules.GHWorkflow

object runtime {

  object http {

    implicit def activities[F[_]: Applicative]: ActivityAlg[F] = new ActivityInterpreter[F]

    implicit def auth[F[_]: Applicative]: AuthAlg[F] = new AuthInterpreter[F]

    implicit def gists[F[_]: Applicative]: GistAlg[F] = new GistInterpreter[F]

    implicit def gitData[F[_]: Applicative]: GitDataAlg[F] = new GitDataInterpreter[F]

    implicit def issues[F[_]: Applicative]: IssuesAlg[F] = new IssueInterpreter[F]

    implicit def organizations[F[_]: Applicative]: OrganizationAlg[F] =
      new OrganizationInterpreter[F]

    implicit def pullRequests[F[_]: Applicative]: PullRequestAlg[F] = new PullRequestInterpreter[F]

    implicit def repos[F[_]: Applicative]: RepositoryAlg[F] = new RepositoryInterpreter[F]

    implicit def users[F[_]: Applicative]: UserAlg[F] = new UserInterpreter[F]

    implicit def workflow[F[_]: Applicative](implicit U: UserAlg[F]): GHWorkflow[F] =
      GHWorkflow.impl[F](U)

  }
}