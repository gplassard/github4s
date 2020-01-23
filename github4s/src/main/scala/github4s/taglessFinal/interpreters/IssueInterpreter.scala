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
import github4s.api.Issues
import github4s.taglessFinal.algebra.IssuesAlg
import github4s.taglessFinal.domain.{
  Comment,
  Issue,
  Label,
  Pagination,
  SearchIssuesResult,
  SearchParam,
  User
}

class IssueInterpreter[F[_]: Applicative](accessToken: Option[String] = None)(
    implicit issues: Issues[F])
    extends IssuesAlg[F] {

  override def listIssues(
      owner: String,
      repo: String,
      headers: Map[String, String] = Map()): F[GHResponse[List[Issue]]] =
    issues.list(accessToken, headers, owner, repo)

  override def getIssue(
      owner: String,
      repo: String,
      number: Int,
      headers: Map[String, String] = Map()): F[GHResponse[Issue]] =
    issues.get(accessToken, headers, owner, repo, number)

  override def searchIssues(
      query: String,
      searchParams: List[SearchParam],
      headers: Map[String, String] = Map()): F[GHResponse[SearchIssuesResult]] =
    issues.search(accessToken, headers, query, searchParams)

  override def createIssue(
      owner: String,
      repo: String,
      title: String,
      body: String,
      milestone: Option[Int],
      labels: List[String],
      assignees: List[String],
      headers: Map[String, String] = Map()): F[GHResponse[Issue]] =
    issues.create(accessToken, headers, owner, repo, title, body, milestone, labels, assignees)

  override def editIssue(
      owner: String,
      repo: String,
      issue: Int,
      state: String,
      title: String,
      body: String,
      milestone: Option[Int],
      labels: List[String],
      assignees: List[String],
      headers: Map[String, String] = Map()): F[GHResponse[Issue]] =
    issues.edit(
      accessToken,
      headers,
      owner,
      repo,
      issue,
      state,
      title,
      body,
      milestone,
      labels,
      assignees)

  override def listComments(
      owner: String,
      repo: String,
      number: Int,
      headers: Map[String, String] = Map()): F[GHResponse[List[Comment]]] =
    issues.listComments(accessToken, headers, owner, repo, number)

  override def createComment(
      owner: String,
      repo: String,
      number: Int,
      body: String,
      headers: Map[String, String] = Map()): F[GHResponse[Comment]] =
    issues.createComment(accessToken, headers, owner, repo, number, body)

  override def editComment(
      owner: String,
      repo: String,
      id: Int,
      body: String,
      headers: Map[String, String] = Map()): F[GHResponse[Comment]] =
    issues.editComment(accessToken, headers, owner, repo, id, body)

  override def deleteComment(
      owner: String,
      repo: String,
      id: Int,
      headers: Map[String, String] = Map()): F[GHResponse[Unit]] =
    issues.deleteComment(accessToken, headers, owner, repo, id)

  override def listLabels(
      owner: String,
      repo: String,
      number: Int,
      headers: Map[String, String] = Map()): F[GHResponse[List[Label]]] =
    issues.listLabels(accessToken, headers, owner, repo, number)

  override def addLabels(
      owner: String,
      repo: String,
      number: Int,
      labels: List[String],
      headers: Map[String, String] = Map()): F[GHResponse[List[Label]]] =
    issues.addLabels(accessToken, headers, owner, repo, number, labels)

  override def removeLabel(
      owner: String,
      repo: String,
      number: Int,
      label: String,
      headers: Map[String, String] = Map()): F[GHResponse[List[Label]]] =
    issues.removeLabel(accessToken, headers, owner, repo, number, label)

  override def listAvailableAssignees(
      owner: String,
      repo: String,
      pagination: Option[Pagination],
      headers: Map[String, String] = Map()): F[GHResponse[List[User]]] =
    issues.listAvailableAssignees(accessToken, headers, owner, repo, pagination)
}
