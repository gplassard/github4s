package github4s.taglessFinal.interpreters

import cats.Applicative
import github4s.GithubResponses.GHResponse
import github4s.api.Gists
import github4s.taglessFinal.algebra.GistAlg
import github4s.taglessFinal.domain.{EditGistFile, Gist, GistFile}
import github4s.GithubDefaultUrls._

class GistInterpreter[F[_]: Applicative] extends GistAlg[F] {

  val gist = new Gists[F]()

  override def newGist(description: String, public: Boolean, files: Map[String, GistFile], accessToken: Option[String]): F[GHResponse[Gist]] = gist.newGist(description,public,files,Map(),accessToken)

  override def getGist(gistId: String, sha: Option[String], accessToken: Option[String]): F[GHResponse[Gist]] = gist.getGist(gistId,sha,Map(),accessToken)

  override def editGist(gistId: String, description: String, files: Map[String, Option[EditGistFile]], accessToken: Option[String]): F[GHResponse[Gist]] = gist.editGist(gistId,description,files,Map(),accessToken)

}
