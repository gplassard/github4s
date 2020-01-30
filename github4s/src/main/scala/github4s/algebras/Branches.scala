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

package github4s.algebras

import github4s.GithubResponses
import github4s.domain._

trait Branches[F[_]] {

  /**
   * Get information of a particular repository
   *
   * @param headers optional user headers to include in the request
   * @param owner   of the repo
   * @param repo    name of the repo
   * @return GHResponse[Repository] repository details
   */
  def getBranchProtection(
      owner: String,
      repo: String,
      branch: String,
      headers: Map[String, String] = Map()
  ): F[GithubResponses.GHResponse[BranchProtection]]

  def updateBranchProtection(
      owner: String,
      repo: String,
      branch: String,
      protection: BranchProtection,
      headers: Map[String, String] = Map()
  ): F[GithubResponses.GHResponse[BranchProtection]]

  def removeBranchProtection(
      owner: String,
      repo: String,
      branch: String,
      headers: Map[String, String] = Map()
  ): F[GithubResponses.GHResponse[Unit]]

  def updateRequiredStatusChecks(
      owner: String,
      repo: String,
      branch: String,
      requiredStatusChecks: RequiredStatusChecks,
      headers: Map[String, String] = Map()
  ): F[GithubResponses.GHResponse[RequiredStatusChecks]]

  def removeRequiredStatusChecks(
      owner: String,
      repo: String,
      branch: String,
      headers: Map[String, String] = Map()
  ): F[GithubResponses.GHResponse[Unit]]

  def listRequiredStatusChecksContexts(
      owner: String,
      repo: String,
      branch: String,
      headers: Map[String, String] = Map()
  ): F[GithubResponses.GHResponse[List[String]]]

}
