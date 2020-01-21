package github4s.taglessFinal.algebra

import github4s.GithubResponses.GHResponse
import github4s.taglessFinal.domain.{Pagination,Subscription,Stargazer,StarredRepository}

abstract class ActivityAlg[F[_]] {
  /**
   * Activities ops ADT
   */
  sealed trait ActivityOp[A]

  final case class SetThreadSub(
                                 id: Int,
                                 subscribed: Boolean,
                                 ignored: Boolean,
                                 accessToken: Option[String] = None
                               ) extends ActivityOp[GHResponse[Subscription]]

  final case class ListStargazers(
                                   owner: String,
                                   repo: String,
                                   timeline: Boolean,
                                   pagination: Option[Pagination] = None,
                                   accessToken: Option[String] = None
                                 ) extends ActivityOp[GHResponse[List[Stargazer]]]

  final case class ListStarredRepositories(
                                            username: String,
                                            timeline: Boolean,
                                            sort: Option[String] = None,
                                            direction: Option[String] = None,
                                            pagination: Option[Pagination] = None,
                                            accessToken: Option[String] = None
                                          ) extends ActivityOp[GHResponse[List[StarredRepository]]]


  def setThreadSub(
                    id: Int,
                    subscribed: Boolean,
                    ignored: Boolean,
                    accessToken: Option[String] = None): F[GHResponse[Subscription]]

  def listStargazers(
                      owner: String,
                      repo: String,
                      timeline: Boolean,
                      pagination: Option[Pagination] = None,
                      accessToken: Option[String] = None): F[GHResponse[List[Stargazer]]]

  def listStarredRepositories(
                               username: String,
                               timeline: Boolean,
                               sort: Option[String] = None,
                               direction: Option[String] = None,
                               pagination: Option[Pagination] = None,
                               accessToken: Option[String] = None): F[GHResponse[List[StarredRepository]]]

}
