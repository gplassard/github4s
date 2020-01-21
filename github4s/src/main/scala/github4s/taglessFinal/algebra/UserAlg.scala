package github4s.taglessFinal.algebra

import github4s.GithubResponses.GHResponse
import github4s.taglessFinal.domain.{User,Pagination}

abstract class UserAlg[F[_]] {

  def getUser(username: String, accessToken: Option[String] = None): F[GHResponse[User]]

  def getAuthUser(accessToken: Option[String] = None): F[GHResponse[User]]

  def getUsers(
                since: Int,
                pagination: Option[Pagination] = None,
                accessToken: Option[String] = None): F[GHResponse[List[User]]]

  def getFollowing(username: String, accessToken: Option[String] = None): F[GHResponse[List[User]]]
}
