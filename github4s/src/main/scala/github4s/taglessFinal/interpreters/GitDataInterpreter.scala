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
import github4s.api.GitData
import github4s.taglessFinal.algebra.GitDataAlg
import github4s.taglessFinal.domain.{Ref, RefAuthor, RefCommit, RefInfo, Tag, TreeData, TreeResult}

class GitDataInterpreter[F[_]: Applicative](accessToken: Option[String] = None)(
    implicit gitdata: GitData[F])
    extends GitDataAlg[F] {
  override def getReference(
      owner: String,
      repo: String,
      ref: String,
      headers: Map[String, String] = Map()): F[GHResponse[NonEmptyList[Ref]]] =
    gitdata.reference(accessToken, headers, owner, repo, ref)

  override def createReference(
      owner: String,
      repo: String,
      ref: String,
      sha: String,
      headers: Map[String, String] = Map()): F[GHResponse[Ref]] =
    gitdata.createReference(accessToken, headers, owner, repo, ref, sha)

  override def updateReference(
      owner: String,
      repo: String,
      ref: String,
      sha: String,
      force: Boolean,
      headers: Map[String, String] = Map()): F[GHResponse[Ref]] =
    gitdata.updateReference(accessToken, headers, owner, repo, ref, sha, force)

  override def getCommit(
      owner: String,
      repo: String,
      sha: String,
      headers: Map[String, String] = Map()): F[GHResponse[RefCommit]] =
    gitdata.commit(accessToken, headers, owner, repo, sha)

  override def createCommit(
      owner: String,
      repo: String,
      message: String,
      tree: String,
      parents: List[String],
      author: Option[RefAuthor],
      headers: Map[String, String] = Map()): F[GHResponse[RefCommit]] =
    gitdata.createCommit(accessToken, headers, owner, repo, message, tree, parents, author)

  override def createBlob(
      owner: String,
      repo: String,
      content: String,
      encoding: Option[String],
      headers: Map[String, String] = Map()): F[GHResponse[RefInfo]] =
    gitdata.createBlob(accessToken, headers, owner, repo, content, encoding)

  override def getTree(
      owner: String,
      repo: String,
      sha: String,
      recursive: Boolean,
      headers: Map[String, String] = Map()): F[GHResponse[TreeResult]] =
    gitdata.tree(accessToken, headers, owner, repo, sha, recursive)

  override def createTree(
      owner: String,
      repo: String,
      baseTree: Option[String],
      treeDataList: List[TreeData],
      headers: Map[String, String] = Map()): F[GHResponse[TreeResult]] =
    gitdata.createTree(accessToken, headers, owner, repo, baseTree, treeDataList)

  override def createTag(
      owner: String,
      repo: String,
      tag: String,
      message: String,
      objectSha: String,
      objectType: String,
      author: Option[RefAuthor],
      headers: Map[String, String] = Map()): F[GHResponse[Tag]] =
    gitdata.createTag(
      accessToken,
      headers,
      owner,
      repo,
      tag,
      message,
      objectSha,
      objectType,
      author)
}
