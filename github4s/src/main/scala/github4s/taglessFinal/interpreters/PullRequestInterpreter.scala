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
import github4s.api.PullRequests
import github4s.taglessFinal.algebra.PullRequestAlg
import github4s.taglessFinal.domain.{
  NewPullRequest,
  PRFilter,
  Pagination,
  PullRequest,
  PullRequestFile,
  PullRequestReview
}

class PullRequestInterpreter[F[_]: Applicative](accessToken: Option[String] = None)(
    implicit pr: PullRequests[F])
    extends PullRequestAlg[F] {

  override def get(
      owner: String,
      repo: String,
      number: Int,
      headers: Map[String, String] = Map()): F[GHResponse[PullRequest]] =
    pr.get(accessToken, headers, owner, repo, number)

  override def list(
      owner: String,
      repo: String,
      filters: List[PRFilter],
      headers: Map[String, String] = Map(),
      pagination: Option[Pagination]): F[GHResponse[List[PullRequest]]] =
    pr.list(accessToken, headers, owner, repo, filters, pagination)

  override def listFiles(
      owner: String,
      repo: String,
      number: Int,
      headers: Map[String, String] = Map(),
      pagination: Option[Pagination]): F[GHResponse[List[PullRequestFile]]] =
    pr.listFiles(accessToken, headers, owner, repo, number, pagination)

  override def create(
      owner: String,
      repo: String,
      newPullRequest: NewPullRequest,
      head: String,
      base: String,
      maintainerCanModify: Option[Boolean],
      headers: Map[String, String] = Map()): F[GHResponse[PullRequest]] =
    pr.create(accessToken, headers, owner, repo, newPullRequest, head, base, maintainerCanModify)

  override def listReviews(
      owner: String,
      repo: String,
      pullRequest: Int,
      headers: Map[String, String] = Map(),
      pagination: Option[Pagination]): F[GHResponse[List[PullRequestReview]]] =
    pr.listReviews(accessToken, headers, owner, repo, pullRequest, pagination)

  override def getReview(
      owner: String,
      repo: String,
      pullRequest: Int,
      review: Int,
      headers: Map[String, String] = Map()): F[GHResponse[PullRequestReview]] =
    pr.getReview(accessToken, headers, owner, repo, pullRequest, review)
}
