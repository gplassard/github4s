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

package github4s.taglessFinal.algebra

import cats.data.NonEmptyList
import github4s.GithubResponses.GHResponse
import github4s.taglessFinal.domain.{
  Branch,
  CombinedStatus,
  Commit,
  Content,
  Pagination,
  Release,
  Repository,
  Status,
  User
}

abstract class RepositoryAlg[F[_]] {

  def get(
      owner: String,
      repo: String,
      headers: Map[String, String] = Map()
  ): F[GHResponse[Repository]]

  def listOrgRepos(
      org: String,
      `type`: Option[String] = None,
      pagination: Option[Pagination] = None,
      headers: Map[String, String] = Map()
  ): F[GHResponse[List[Repository]]]

  def listUserRepos(
      user: String,
      `type`: Option[String] = None,
      pagination: Option[Pagination] = None,
      headers: Map[String, String] = Map()
  ): F[GHResponse[List[Repository]]]

  def getContents(
      owner: String,
      repo: String,
      path: String,
      ref: Option[String] = None,
      headers: Map[String, String] = Map()
  ): F[GHResponse[NonEmptyList[Content]]]

  def listCommits(
      owner: String,
      repo: String,
      sha: Option[String] = None,
      path: Option[String] = None,
      author: Option[String] = None,
      since: Option[String] = None,
      until: Option[String] = None,
      pagination: Option[Pagination] = None,
      headers: Map[String, String] = Map()
  ): F[GHResponse[List[Commit]]]

  def listBranches(
      owner: String,
      repo: String,
      `protected`: Option[Boolean] = None,
      headers: Map[String, String] = Map()
  ): F[GHResponse[List[Branch]]]

  def listContributors(
      owner: String,
      repo: String,
      anon: Option[String] = None,
      headers: Map[String, String] = Map()
  ): F[GHResponse[List[User]]]

  def listCollaborators(
      owner: String,
      repo: String,
      affiliation: Option[String] = None,
      headers: Map[String, String] = Map()
  ): F[GHResponse[List[User]]]

  def createRelease(
      owner: String,
      repo: String,
      tagName: String,
      name: String,
      body: String,
      targetCommitish: Option[String] = None,
      draft: Option[Boolean] = None,
      prerelease: Option[Boolean] = None,
      headers: Map[String, String] = Map()
  ): F[GHResponse[Release]]

  def getCombinedStatus(
      owner: String,
      repo: String,
      ref: String,
      headers: Map[String, String] = Map()
  ): F[GHResponse[CombinedStatus]]

  def listStatuses(
      owner: String,
      repo: String,
      ref: String,
      headers: Map[String, String] = Map()
  ): F[GHResponse[List[Status]]]

  def createStatus(
      owner: String,
      repo: String,
      sha: String,
      state: String,
      target_url: Option[String],
      description: Option[String],
      context: Option[String],
      headers: Map[String, String] = Map()
  ): F[GHResponse[Status]]
}
