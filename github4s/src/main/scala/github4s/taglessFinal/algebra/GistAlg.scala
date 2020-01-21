package github4s.taglessFinal.algebra

import github4s.GithubResponses.GHResponse
import github4s.taglessFinal.domain.{Gist,GistFile,EditGistFile}

abstract class GistAlg[F[_]] {

  /**
   * Gist ops ADT
   */
  sealed trait GistOp[A]

  final case class NewGist(
                            description: String,
                            public: Boolean,
                            files: Map[String, GistFile],
                            accessToken: Option[String] = None
                          ) extends GistOp[GHResponse[Gist]]

  final case class GetGist(
                            gistId: String,
                            sha: Option[String] = None,
                            accessToken: Option[String] = None
                          ) extends GistOp[GHResponse[Gist]]

  final case class EditGist(
                             gistId: String,
                             description: String,
                             files: Map[String, Option[EditGistFile]],
                             accessToken: Option[String] = None
                           ) extends GistOp[GHResponse[Gist]]

  def newGist(
               description: String,
               public: Boolean,
               files: Map[String, GistFile],
               accessToken: Option[String] = None
             ): F[GHResponse[Gist]]

  def getGist(
               gistId: String,
               sha: Option[String] = None,
               accessToken: Option[String] = None
             ): F[GHResponse[Gist]]

  def editGist(
                gistId: String,
                description: String,
                files: Map[String, Option[EditGistFile]],
                accessToken: Option[String] = None
              ): F[GHResponse[Gist]]
}
