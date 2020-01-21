package github4s.taglessFinal.algebra

import github4s.GithubResponses.GHResponse
import github4s.taglessFinal.domain.{Authorization,Authorize,OAuthToken}

abstract class AuthAlg[F[_]] {
  /**
   * Auths ops ADT
   */
  sealed trait AuthOp[A]

  final case class NewAuth(
                            username: String,
                            password: String,
                            scopes: List[String],
                            note: String,
                            client_id: String,
                            client_secret: String
                          ) extends AuthOp[GHResponse[Authorization]]

  final case class AuthorizeUrl(
                                 client_id: String,
                                 redirect_uri: String,
                                 scopes: List[String]
                               ) extends AuthOp[GHResponse[Authorize]]

  final case class GetAccessToken(
                                   client_id: String,
                                   client_secret: String,
                                   code: String,
                                   redirect_uri: String,
                                   state: String
                                 ) extends AuthOp[GHResponse[OAuthToken]

  def newAuth(
               username: String,
               password: String,
               scopes: List[String],
               note: String,
               client_id: String,
               client_secret: String
             ): F[GHResponse[Authorization]]

  def authorizeUrl(
                    client_id: String,
                    redirect_uri: String,
                    scopes: List[String]
                  ): F[GHResponse[Authorize]]

  def getAccessToken(
                      client_id: String,
                      client_secret: String,
                      code: String,
                      redirect_uri: String,
                      state: String
                    ): F[GHResponse[OAuthToken]]
}
