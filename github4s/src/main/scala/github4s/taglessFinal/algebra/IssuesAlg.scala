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
  Comment,
  Issue,
  Label,
  Pagination,
  SearchIssuesResult,
  SearchParam,
  User
}

abstract class IssuesAlg[F[_]] {

  def listIssues(
      owner: String,
      repo: String,
      headers: Map[String, String] = Map()
  ): F[GHResponse[List[Issue]]]

  def getIssue(
      owner: String,
      repo: String,
      number: Int,
      headers: Map[String, String] = Map()
  ): F[GHResponse[Issue]]

  def searchIssues(
      query: String,
      searchParams: List[SearchParam],
      headers: Map[String, String] = Map()
  ): F[GHResponse[SearchIssuesResult]]

  def createIssue(
      owner: String,
      repo: String,
      title: String,
      body: String,
      milestone: Option[Int],
      labels: List[String],
      assignees: List[String],
      headers: Map[String, String] = Map()
  ): F[GHResponse[Issue]]

  def editIssue(
      owner: String,
      repo: String,
      issue: Int,
      state: String,
      title: String,
      body: String,
      milestone: Option[Int],
      labels: List[String],
      assignees: List[String],
      headers: Map[String, String] = Map()
  ): F[GHResponse[Issue]]

  def listComments(
      owner: String,
      repo: String,
      number: Int,
      headers: Map[String, String] = Map()
  ): F[GHResponse[List[Comment]]]

  def createComment(
      owner: String,
      repo: String,
      number: Int,
      body: String,
      headers: Map[String, String] = Map()
  ): F[GHResponse[Comment]]

  def editComment(
      owner: String,
      repo: String,
      id: Int,
      body: String,
      headers: Map[String, String] = Map()
  ): F[GHResponse[Comment]]

  def deleteComment(
      owner: String,
      repo: String,
      id: Int,
      headers: Map[String, String] = Map()
  ): F[GHResponse[Unit]]

  def listLabels(
      owner: String,
      repo: String,
      number: Int,
      headers: Map[String, String] = Map()
  ): F[GHResponse[List[Label]]]

  def addLabels(
      owner: String,
      repo: String,
      number: Int,
      labels: List[String],
      headers: Map[String, String] = Map()
  ): F[GHResponse[List[Label]]]

  def removeLabel(
      owner: String,
      repo: String,
      number: Int,
      label: String,
      headers: Map[String, String] = Map()
  ): F[GHResponse[List[Label]]]

  def listAvailableAssignees(
      owner: String,
      repo: String,
      pagination: Option[Pagination] = None,
      headers: Map[String, String] = Map()
  ): F[GHResponse[List[User]]]
}