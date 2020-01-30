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

package github4s.interpreters

import github4s.GithubResponses.GHResponse
import github4s.algebras.Branches
import github4s.Decoders._
import github4s.domain._
import github4s.Encoders._
import github4s.domain.{BranchProtection, RequiredStatusChecks}
import github4s.http.HttpClient

class BranchesInterpreter[F[_]](implicit client: HttpClient[F], accessToken: Option[String])
    extends Branches[F] {

  override def getBranchProtection(
      owner: String,
      repo: String,
      branch: String,
      headers: Map[String, String] = Map()
  ): F[GHResponse[BranchProtection]] =
    client.get[BranchProtection](
      accessToken,
      s"repos/$owner/$repo/branches/$branch/protection",
      headers
    )

  override def updateBranchProtection(
      owner: String,
      repo: String,
      branch: String,
      protection: BranchProtection,
      headers: Map[String, String] = Map()
  ): F[GHResponse[BranchProtection]] =
    client.put[BranchProtection, BranchProtection](
      accessToken,
      s"repos/$owner/$repo/branches/$branch/protection",
      headers,
      data = protection
    )

  override def removeBranchProtection(
      owner: String,
      repo: String,
      branch: String,
      headers: Map[String, String] = Map()
  ): F[GHResponse[Unit]] =
    client.delete(accessToken, s"repos/$owner/$repo/branches/$branch/protection", headers)

  override def updateRequiredStatusChecks(
      owner: String,
      repo: String,
      branch: String,
      requiredStatusChecks: RequiredStatusChecks,
      headers: Map[String, String] = Map()
  ): F[GHResponse[RequiredStatusChecks]] =
    client.patch[RequiredStatusChecks, RequiredStatusChecks](
      accessToken,
      s"repos/$owner/$repo/branches/$branch/protection/required_status_checks",
      headers,
      data = requiredStatusChecks
    )

  override def removeRequiredStatusChecks(
      owner: String,
      repo: String,
      branch: String,
      headers: Map[String, String] = Map()
  ): F[GHResponse[Unit]] =
    client.delete(
      accessToken,
      s"repos/$owner/$repo/branches/$branch/protection/required_status_checks",
      headers
    )

  override def listRequiredStatusChecksContexts(
      owner: String,
      repo: String,
      branch: String,
      headers: Map[String, String] = Map()
  ): F[GHResponse[List[String]]] =
    client.get[List[String]](
      accessToken,
      s"repos/$owner/$repo/branches/$branch/protection/required_status_checks/contexts",
      headers
    )

}
