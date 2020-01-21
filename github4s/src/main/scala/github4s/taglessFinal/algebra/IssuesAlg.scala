package github4s.taglessFinal.algebra

import github4s.GithubResponses.GHResponse
import github4s.taglessFinal.domain.{Issue,SearchParam,SearchIssuesResult,Comment,Label,Pagination,User}

abstract class IssuesAlg[F[_]]{

  /**
   * Issues ops ADT
   */
  sealed trait IssueOp[A]

  final case class ListIssues(
                               owner: String,
                               repo: String,
                               accessToken: Option[String] = None
                             ) extends IssueOp[GHResponse[List[Issue]]]

  final case class GetIssue(
                             owner: String,
                             repo: String,
                             number: Int,
                             accessToken: Option[String] = None
                           ) extends IssueOp[GHResponse[Issue]]

  final case class SearchIssues(
                                 query: String,
                                 searchParams: List[SearchParam],
                                 accessToken: Option[String] = None
                               ) extends IssueOp[GHResponse[SearchIssuesResult]]

  final case class CreateIssue(
                                owner: String,
                                repo: String,
                                title: String,
                                body: String,
                                milestone: Option[Int],
                                labels: List[String],
                                assignees: List[String],
                                accessToken: Option[String] = None
                              ) extends IssueOp[GHResponse[Issue]]

  final case class EditIssue(
                              owner: String,
                              repo: String,
                              issue: Int,
                              state: String,
                              title: String,
                              body: String,
                              milestone: Option[Int],
                              labels: List[String],
                              assignees: List[String],
                              accessToken: Option[String] = None
                            ) extends IssueOp[GHResponse[Issue]]

  final case class ListComments(
                                 owner: String,
                                 repo: String,
                                 number: Int,
                                 accessToken: Option[String] = None
                               ) extends IssueOp[GHResponse[List[Comment]]]

  final case class CreateComment(
                                  owner: String,
                                  repo: String,
                                  number: Int,
                                  body: String,
                                  accessToken: Option[String] = None
                                ) extends IssueOp[GHResponse[Comment]]

  final case class EditComment(
                                owner: String,
                                repo: String,
                                id: Int,
                                body: String,
                                accessToken: Option[String] = None
                              ) extends IssueOp[GHResponse[Comment]]

  final case class DeleteComment(
                                  owner: String,
                                  repo: String,
                                  id: Int,
                                  accessToken: Option[String] = None
                                ) extends IssueOp[GHResponse[Unit]]

  final case class ListLabels(
                               owner: String,
                               repo: String,
                               number: Int,
                               accessToken: Option[String] = None
                             ) extends IssueOp[GHResponse[List[Label]]]

  final case class AddLabels(
                              owner: String,
                              repo: String,
                              number: Int,
                              labels: List[String],
                              accessToken: Option[String] = None
                            ) extends IssueOp[GHResponse[List[Label]]]

  final case class RemoveLabel(
                                owner: String,
                                repo: String,
                                number: Int,
                                label: String,
                                accessToken: Option[String] = None
                              ) extends IssueOp[GHResponse[List[Label]]]

  final case class ListAvailableAssignees(
                                           owner: String,
                                           repo: String,
                                           pagination: Option[Pagination] = None,
                                           accessToken: Option[String] = None
                                         ) extends IssueOp[GHResponse[List[User]]]

  def listIssues(
                  owner: String,
                  repo: String,
                  accessToken: Option[String] = None
                ): F[GHResponse[List[Issue]]]

  def getIssue(
                owner: String,
                repo: String,
                number: Int,
                accessToken: Option[String] = None
              ): F[GHResponse[Issue]]

  def searchIssues(
                    query: String,
                    searchParams: List[SearchParam],
                    accessToken: Option[String] = None
                  ): F[GHResponse[SearchIssuesResult]]

  def createIssue(
                   owner: String,
                   repo: String,
                   title: String,
                   body: String,
                   milestone: Option[Int],
                   labels: List[String],
                   assignees: List[String],
                   accessToken: Option[String] = None
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
                 accessToken: Option[String] = None
               ): F[GHResponse[Issue]]

  def listComments(
                    owner: String,
                    repo: String,
                    number: Int,
                    accessToken: Option[String] = None
                  ): F[GHResponse[List[Comment]]]

  def createComment(
                     owner: String,
                     repo: String,
                     number: Int,
                     body: String,
                     accessToken: Option[String] = None
                   ): F[GHResponse[Comment]]

  def editComment(
                   owner: String,
                   repo: String,
                   id: Int,
                   body: String,
                   accessToken: Option[String] = None
                 ): F[GHResponse[Comment]]

  def deleteComment(
                     owner: String,
                     repo: String,
                     id: Int,
                     accessToken: Option[String] = None
                   ): F[GHResponse[Unit]]

  def listLabels(
                  owner: String,
                  repo: String,
                  number: Int,
                  accessToken: Option[String] = None
                ): F[GHResponse[List[Label]]]

  def addLabels(
                 owner: String,
                 repo: String,
                 number: Int,
                 labels: List[String],
                 accessToken: Option[String] = None
               ): F[GHResponse[List[Label]]]

  def removeLabel(
                   owner: String,
                   repo: String,
                   number: Int,
                   label: String,
                   accessToken: Option[String] = None
                 ): F[GHResponse[List[Label]]]

  def listAvailableAssignees(
                              owner: String,
                              repo: String,
                              pagination: Option[Pagination] = None,
                              accessToken: Option[String] = None
                            ): F[GHResponse[List[User]]]
}
