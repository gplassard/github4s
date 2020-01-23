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
import github4s.api.Auth
import github4s.taglessFinal.algebra.AuthAlg
import github4s.taglessFinal.domain.{Authorization, Authorize, OAuthToken}

class AuthInterpreter[F[_]: Applicative](accessToken: Option[String] = None)(implicit auth: Auth[F])
    extends AuthAlg[F] {
  override def newAuth(
      username: String,
      password: String,
      scopes: List[String],
      note: String,
      client_id: String,
      client_secret: String,
      headers: Map[String, String] = Map()): F[GHResponse[Authorization]] =
    auth.newAuth(username, password, scopes, note, client_id, client_secret, headers)

  override def authorizeUrl(
      client_id: String,
      redirect_uri: String,
      scopes: List[String],
      headers: Map[String, String] = Map()): F[GHResponse[Authorize]] =
    auth.authorizeUrl(client_id, redirect_uri, scopes)

  override def getAccessToken(
      client_id: String,
      client_secret: String,
      code: String,
      redirect_uri: String,
      state: String,
      headers: Map[String, String] = Map()): F[GHResponse[OAuthToken]] =
    auth.getAccessToken(client_id, client_secret, code, redirect_uri, state, headers)
}
