package github4s.taglessFinal.interpreters

import cats.Applicative
import github4s.GithubResponses.GHResponse
import github4s.api.Organizations
import github4s.taglessFinal.algebra.OrganizationAlg
import github4s.taglessFinal.domain.{Pagination, User}
import github4s.GithubDefaultUrls._

class OrganizationInterpreter[F[_]: Applicative] extends OrganizationAlg[F] {

  val orgs = new Organizations[F]()

  override def listMembers(org: String, filter: Option[String], role: Option[String], pagination: Option[Pagination], accessToken: Option[String]): F[GHResponse[List[User]]] = orgs.listMembers(accessToken,Map(),org,filter,role,pagination)

  override def listOutsideCollaborators(org: String, filter: Option[String], pagination: Option[Pagination], accessToken: Option[String]): F[GHResponse[List[User]]] = orgs.listOutsideCollaborators(accessToken,Map(),org,filter,pagination)

}
