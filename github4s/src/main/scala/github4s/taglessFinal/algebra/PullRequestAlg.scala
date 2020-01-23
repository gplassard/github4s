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

import github4s.GithubResponses.GHResponse
import github4s.taglessFinal.domain.{
  NewPullRequest,
  PRFilter,
  Pagination,
  PullRequest,
  PullRequestFile,
  PullRequestReview
}

abstract class PullRequestAlg[F[_]] {

  def get(
      owner: String,
      repo: String,
      number: Int,
      headers: Map[String, String] = Map()
  ): F[GHResponse[PullRequest]]

  def list(
      owner: String,
      repo: String,
      filters: List[PRFilter] = Nil,
      headers: Map[String, String] = Map(),
      pagination: Option[Pagination] = None
  ): F[GHResponse[List[PullRequest]]]

  def listFiles(
      owner: String,
      repo: String,
      number: Int,
      headers: Map[String, String] = Map(),
      pagination: Option[Pagination] = None
  ): F[GHResponse[List[PullRequestFile]]]

  def create(
      owner: String,
      repo: String,
      newPullRequest: NewPullRequest,
      head: String,
      base: String,
      maintainerCanModify: Option[Boolean] = Some(true),
      headers: Map[String, String] = Map()
  ): F[GHResponse[PullRequest]]

  def listReviews(
      owner: String,
      repo: String,
      pullRequest: Int,
      headers: Map[String, String] = Map(),
      pagination: Option[Pagination] = None): F[GHResponse[List[PullRequestReview]]]

  def getReview(
      owner: String,
      repo: String,
      pullRequest: Int,
      review: Int,
      headers: Map[String, String] = Map()): F[GHResponse[PullRequestReview]]

}
