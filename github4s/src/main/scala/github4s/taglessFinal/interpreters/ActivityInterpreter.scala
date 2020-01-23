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

package github4s.taglessFinal.interpreters

import cats.Applicative
import github4s.GithubResponses.GHResponse
import github4s.api.Activities
import github4s.taglessFinal.algebra.ActivityAlg
import github4s.taglessFinal.domain.{Pagination, Stargazer, StarredRepository, Subscription}

class ActivityInterpreter[F[_]: Applicative](accessToken: Option[String] = None)(
    implicit act: Activities[F])
    extends ActivityAlg[F] {
  override def setThreadSub(
      id: Int,
      subscribed: Boolean,
      ignored: Boolean,
      headers: Map[String, String] = Map()): F[GHResponse[Subscription]] =
    act.setThreadSub(accessToken, headers, id, subscribed, ignored)

  override def listStargazers(
      owner: String,
      repo: String,
      timeline: Boolean,
      pagination: Option[Pagination],
      headers: Map[String, String] = Map()): F[GHResponse[List[Stargazer]]] =
    act.listStargazers(accessToken, headers, owner, repo, timeline, pagination)

  override def listStarredRepositories(
      username: String,
      timeline: Boolean,
      sort: Option[String],
      direction: Option[String],
      pagination: Option[Pagination],
      headers: Map[String, String] = Map()): F[GHResponse[List[StarredRepository]]] =
    act.listStarredRepositories(
      accessToken,
      headers,
      username,
      timeline,
      sort,
      direction,
      pagination)
}
