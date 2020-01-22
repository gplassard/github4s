package github4s.taglessFinal.interpreters

import cats.Applicative
import github4s.GithubResponses.GHResponse
import github4s.api.PullRequests
import github4s.taglessFinal.algebra.PullRequestAlg
import github4s.taglessFinal.domain.{NewPullRequest, PRFilter, Pagination, PullRequest, PullRequestFile, PullRequestReview}
import github4s.GithubDefaultUrls._

class PullRequestInterpreter[F[_]: Applicative] extends PullRequestAlg[F] {

  val pr = new PullRequests[F]
  override def getPullRequest(owner: String, repo: String, number: Int, accessToken: Option[String]): F[GHResponse[PullRequest]] = pr.get(accessToken,Map(),owner,repo,number)

  override def listPullRequests(owner: String, repo: String, filters: List[PRFilter], accessToken: Option[String], pagination: Option[Pagination]): F[GHResponse[List[PullRequest]]] = pr.list(accessToken,Map(),owner,repo,filters,pagination)

  override def listPullRequestFiles(owner: String, repo: String, number: Int, accessToken: Option[String], pagination: Option[Pagination]): F[GHResponse[List[PullRequestFile]]] = pr.listFiles(accessToken,Map(),owner,repo,number,pagination)

  override def createPullRequest(owner: String, repo: String, newPullRequest: NewPullRequest, head: String, base: String, maintainerCanModify: Option[Boolean], accessToken: Option[String]): F[GHResponse[PullRequest]] = pr.create(accessToken,Map(),owner,repo,newPullRequest,head,base,maintainerCanModify)

  override def listPullRequestReviews(owner: String, repo: String, pullRequest: Int, accessToken: Option[String], pagination: Option[Pagination]): F[GHResponse[List[PullRequestReview]]] = pr.listReviews(accessToken,Map(),owner,repo,pullRequest,pagination)

  override def getPullRequestReview(owner: String, repo: String, pullRequest: Int, review: Int, accessToken: Option[String]): F[GHResponse[PullRequestReview]] = pr.getReview(accessToken,Map(),owner,repo,pullRequest,review)
}
