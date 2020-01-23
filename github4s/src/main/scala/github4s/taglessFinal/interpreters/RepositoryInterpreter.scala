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
import cats.data.NonEmptyList
import github4s.GithubResponses.GHResponse
import github4s.api.Repos
import github4s.taglessFinal.algebra.RepositoryAlg
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

class RepositoryInterpreter[F[_]: Applicative](accessToken: Option[String] = None)(
    implicit repos: Repos[F])
    extends RepositoryAlg[F] {
  override def get(
      owner: String,
      repo: String,
      headers: Map[String, String] = Map()): F[GHResponse[Repository]] =
    repos.get(accessToken, headers, owner, repo)

  override def listOrgRepos(
      org: String,
      `type`: Option[String],
      pagination: Option[Pagination],
      headers: Map[String, String] = Map()): F[GHResponse[List[Repository]]] =
    repos.listOrgRepos(accessToken, headers, org, `type`, pagination)

  override def listUserRepos(
      user: String,
      `type`: Option[String],
      pagination: Option[Pagination],
      headers: Map[String, String] = Map()): F[GHResponse[List[Repository]]] =
    repos.listUserRepos(accessToken, headers, user, `type`, pagination)

  override def getContents(
      owner: String,
      repo: String,
      path: String,
      ref: Option[String],
      headers: Map[String, String] = Map()): F[GHResponse[NonEmptyList[Content]]] =
    repos.getContents(accessToken, headers, owner, repo, path, ref)

  override def listCommits(
      owner: String,
      repo: String,
      sha: Option[String],
      path: Option[String],
      author: Option[String],
      since: Option[String],
      until: Option[String],
      pagination: Option[Pagination],
      headers: Map[String, String] = Map()): F[GHResponse[List[Commit]]] =
    repos.listCommits(
      accessToken,
      headers,
      owner,
      repo,
      sha,
      path,
      author,
      since,
      until,
      pagination)

  override def listBranches(
      owner: String,
      repo: String,
      `protected`: Option[Boolean],
      headers: Map[String, String] = Map()): F[GHResponse[List[Branch]]] =
    repos.listBranches(accessToken, headers, owner, repo, `protected`)

  override def listContributors(
      owner: String,
      repo: String,
      anon: Option[String],
      headers: Map[String, String] = Map()): F[GHResponse[List[User]]] =
    repos.listContributors(accessToken, headers, owner, repo, anon)

  override def listCollaborators(
      owner: String,
      repo: String,
      affiliation: Option[String],
      headers: Map[String, String] = Map()): F[GHResponse[List[User]]] =
    repos.listCollaborators(accessToken, headers, owner, repo, affiliation)

  override def createRelease(
      owner: String,
      repo: String,
      tagName: String,
      name: String,
      body: String,
      targetCommitish: Option[String],
      draft: Option[Boolean],
      prerelease: Option[Boolean],
      headers: Map[String, String] = Map()): F[GHResponse[Release]] =
    repos.createRelease(
      accessToken,
      headers,
      owner,
      repo,
      tagName,
      name,
      body,
      targetCommitish,
      draft,
      prerelease)

  override def getCombinedStatus(
      owner: String,
      repo: String,
      ref: String,
      headers: Map[String, String] = Map()): F[GHResponse[CombinedStatus]] =
    repos.getStatus(accessToken, headers, owner, repo, ref)

  override def listStatuses(
      owner: String,
      repo: String,
      ref: String,
      headers: Map[String, String] = Map()): F[GHResponse[List[Status]]] =
    repos.listStatuses(accessToken, headers, owner, repo, ref)

  override def createStatus(
      owner: String,
      repo: String,
      sha: String,
      state: String,
      target_url: Option[String],
      description: Option[String],
      context: Option[String],
      headers: Map[String, String] = Map()): F[GHResponse[Status]] =
    repos.createStatus(
      accessToken,
      headers,
      owner,
      repo,
      sha,
      state,
      target_url,
      description,
      context)
}
