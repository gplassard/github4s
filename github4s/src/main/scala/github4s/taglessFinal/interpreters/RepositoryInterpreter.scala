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
import github4s.GithubDefaultUrls._

class RepositoryInterpreter[F[_]: Applicative] extends RepositoryAlg[F] {
  val repos = new Repos[F]()

  override def getRepo(
      owner: String,
      repo: String,
      accessToken: Option[String]): F[GHResponse[Repository]] =
    repos.get(accessToken, Map(), owner, repo)

  override def listOrgRepos(
      org: String,
      `type`: Option[String],
      pagination: Option[Pagination],
      accessToken: Option[String]): F[GHResponse[List[Repository]]] =
    repos.listOrgRepos(accessToken, Map(), org, `type`, pagination)

  override def listUserRepos(
      user: String,
      `type`: Option[String],
      pagination: Option[Pagination],
      accessToken: Option[String]): F[GHResponse[List[Repository]]] =
    repos.listUserRepos(accessToken, Map(), user, `type`, pagination)

  override def getContents(
      owner: String,
      repo: String,
      path: String,
      ref: Option[String],
      accessToken: Option[String]): F[GHResponse[NonEmptyList[Content]]] =
    repos.getContents(accessToken, Map(), owner, repo, path, ref)

  override def listCommits(
      owner: String,
      repo: String,
      sha: Option[String],
      path: Option[String],
      author: Option[String],
      since: Option[String],
      until: Option[String],
      pagination: Option[Pagination],
      accessToken: Option[String]): F[GHResponse[List[Commit]]] =
    repos.listCommits(accessToken, Map(), owner, repo, sha, path, author, since, until, pagination)

  override def listBranches(
      owner: String,
      repo: String,
      `protected`: Option[Boolean],
      accessToken: Option[String]): F[GHResponse[List[Branch]]] =
    repos.listBranches(accessToken, Map(), owner, repo, `protected`)

  override def listContributors(
      owner: String,
      repo: String,
      anon: Option[String],
      accessToken: Option[String]): F[GHResponse[List[User]]] =
    repos.listContributors(accessToken, Map(), owner, repo, anon)

  override def listCollaborators(
      owner: String,
      repo: String,
      affiliation: Option[String],
      accessToken: Option[String]): F[GHResponse[List[User]]] =
    repos.listCollaborators(accessToken, Map(), owner, repo, affiliation)

  override def createRelease(
      owner: String,
      repo: String,
      tagName: String,
      name: String,
      body: String,
      targetCommitish: Option[String],
      draft: Option[Boolean],
      prerelease: Option[Boolean],
      accessToken: Option[String]): F[GHResponse[Release]] =
    repos.createRelease(
      accessToken,
      Map(),
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
      accessToken: Option[String]): F[GHResponse[CombinedStatus]] =
    repos.getStatus(accessToken, Map(), owner, repo, ref)

  override def listStatuses(
      owner: String,
      repo: String,
      ref: String,
      accessToken: Option[String]): F[GHResponse[List[Status]]] =
    repos.listStatuses(accessToken, Map(), owner, repo, ref)

  override def createStatus(
      owner: String,
      repo: String,
      sha: String,
      state: String,
      target_url: Option[String],
      description: Option[String],
      context: Option[String],
      accessToken: Option[String]): F[GHResponse[Status]] =
    repos.createStatus(
      accessToken,
      Map(),
      owner,
      repo,
      sha,
      state,
      target_url,
      description,
      context)
}