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
import github4s.api.Gists
import github4s.taglessFinal.algebra.GistAlg
import github4s.taglessFinal.domain.{EditGistFile, Gist, GistFile}
import github4s.GithubDefaultUrls._

class GistInterpreter[F[_]: Applicative] extends GistAlg[F] {

  val gist = new Gists[F]()

  override def newGist(
      description: String,
      public: Boolean,
      files: Map[String, GistFile],
      accessToken: Option[String]): F[GHResponse[Gist]] =
    gist.newGist(description, public, files, Map(), accessToken)

  override def getGist(
      gistId: String,
      sha: Option[String],
      accessToken: Option[String]): F[GHResponse[Gist]] =
    gist.getGist(gistId, sha, Map(), accessToken)

  override def editGist(
      gistId: String,
      description: String,
      files: Map[String, Option[EditGistFile]],
      accessToken: Option[String]): F[GHResponse[Gist]] =
    gist.editGist(gistId, description, files, Map(), accessToken)

}
