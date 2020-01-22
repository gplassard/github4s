package github4s.taglessFinal.interpreters

import cats.Applicative
import github4s.GithubResponses.GHResponse
import github4s.api.Auth
import github4s.taglessFinal.algebra.AuthAlg
import github4s.taglessFinal.domain.{Authorization, Authorize, OAuthToken}
import github4s.GithubDefaultUrls._

class AuthInterpreter[F[_]: Applicative] extends AuthAlg[F]{

  val auth = new Auth[F]()

  override def newAuth(username: String, password: String, scopes: List[String], note: String, client_id: String, client_secret: String): F[GHResponse[Authorization]] = auth.newAuth(username,password,scopes,note,client_id,client_secret,Map())

  override def authorizeUrl(client_id: String, redirect_uri: String, scopes: List[String]): F[GHResponse[Authorize]] = auth.authorizeUrl(client_id,redirect_uri,scopes)

  override def getAccessToken(client_id: String, client_secret: String, code: String, redirect_uri: String, state: String): F[GHResponse[OAuthToken]] = auth.getAccessToken(client_id,client_secret,code,redirect_uri,state,Map())
}
