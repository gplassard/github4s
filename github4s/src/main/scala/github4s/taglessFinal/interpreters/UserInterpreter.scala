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
import github4s.api.MyUsers
import github4s.taglessFinal.algebra.UserAlg
import github4s.free.domain.{Pagination, User}
import github4s.GithubDefaultUrls._

class UserInterpreter[F[_]: Applicative] extends UserAlg[F] {

  val client = new MyUsers[F]()

  override def getUser(username: String, accessToken: Option[String]): F[GHResponse[User]] =
    client.get(accessToken = accessToken, username = username)

  override def getAuthUser(accessToken: Option[String]): F[GHResponse[User]] =
    client.getAuth(accessToken)

  override def getUsers(
      since: Int,
      pagination: Option[Pagination],
      accessToken: Option[String]): F[GHResponse[List[User]]] =
    client.getUsers(accessToken = accessToken, since = since, pagination = pagination)

  override def getFollowing(
      username: String,
      accessToken: Option[String]): F[GHResponse[List[User]]] =
    client.getFollowing(accessToken = accessToken, username = username)
}
