package github4s.taglessFinal.interpreters

import cats.Applicative
import cats.data.NonEmptyList
import github4s.GithubResponses.GHResponse
import github4s.api.GitData
import github4s.taglessFinal.algebra.GistDataAlg
import github4s.taglessFinal.domain.{Ref, RefAuthor, RefCommit, RefInfo, Tag, TreeData, TreeResult}
import github4s.GithubDefaultUrls._

class GitDataInterpreter[F[_]: Applicative] extends GistDataAlg[F] {

  val gitdata = new GitData[F]()

  override def getReference(owner: String, repo: String, ref: String, accessToken: Option[String]): F[GHResponse[NonEmptyList[Ref]]] = gitdata.reference(accessToken,Map(),owner,repo,ref)

  override def createReference(owner: String, repo: String, ref: String, sha: String, accessToken: Option[String]): F[GHResponse[Ref]] = gitdata.createReference(accessToken,Map(),owner,repo,ref,sha)

  override def updateReference(owner: String, repo: String, ref: String, sha: String, force: Boolean, accessToken: Option[String]): F[GHResponse[Ref]] = gitdata.updateReference(accessToken,Map(),owner,repo,ref,sha,force)

  override def getCommit(owner: String, repo: String, sha: String, accessToken: Option[String]): F[GHResponse[RefCommit]] = gitdata.commit(accessToken,Map(),owner,repo,sha)

  override def createCommit(owner: String, repo: String, message: String, tree: String, parents: List[String], author: Option[RefAuthor], accessToken: Option[String]): F[GHResponse[RefCommit]] = gitdata.createCommit(accessToken,Map(),owner,repo,message,tree,parents,author)

  override def createBlob(owner: String, repo: String, content: String, encoding: Option[String], accessToken: Option[String]): F[GHResponse[RefInfo]] = gitdata.createBlob(accessToken,Map(),owner,repo,content,encoding)

  override def getTree(owner: String, repo: String, sha: String, recursive: Boolean, accessToken: Option[String]): F[GHResponse[TreeResult]] = gitdata.tree(accessToken,Map(),owner,repo,sha,recursive)

  override def createTree(owner: String, repo: String, baseTree: Option[String], treeDataList: List[TreeData], accessToken: Option[String]): F[GHResponse[TreeResult]] = gitdata.createTree(accessToken,Map(),owner,repo,baseTree,treeDataList)

  override def createTag(owner: String, repo: String, tag: String, message: String, objectSha: String, objectType: String, author: Option[RefAuthor], accessToken: Option[String]): F[GHResponse[Tag]] = gitdata.createTag(accessToken,Map(),owner,repo,tag,message,objectSha,objectType,author)
}
