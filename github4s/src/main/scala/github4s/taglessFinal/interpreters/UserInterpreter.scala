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
import github4s.taglessFinal.algebra.UserAlg
import github4s.taglessFinal.domain.{Pagination, User}
import github4s.api.Users

class UserInterpreter[F[_]: Applicative](accessToken: Option[String] = None)(
    implicit user: Users[F])
    extends UserAlg[F] {

  override def get(username: String, headers: Map[String, String] = Map()): F[GHResponse[User]] =
    user.get(accessToken, headers, username)

  override def getAuth(headers: Map[String, String] = Map()): F[GHResponse[User]] =
    user.getAuth(accessToken, headers)

  override def getUsers(
      since: Int,
      pagination: Option[Pagination],
      headers: Map[String, String] = Map()): F[GHResponse[List[User]]] =
    user.getUsers(accessToken, headers, since, pagination)

  override def getFollowing(
      username: String,
      headers: Map[String, String] = Map()): F[GHResponse[List[User]]] =
    user.getFollowing(accessToken, headers, username)
}
