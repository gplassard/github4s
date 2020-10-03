/*
 * Copyright 2016-2020 47 Degrees, LLC. <http://www.47deg.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package github4s.http

import github4s.domain.Pagination
import org.http4s.Request
import org.http4s.client.blaze.BlazeClientBuilder
import cats.effect.{ConcurrentEffect, Resource}
import github4s.GithubResponses.{GHResponse, JsonParsingException}
import cats.implicits._
import io.circe.{Decoder, Encoder}
import org.http4s.circe.CirceEntityDecoder._
import github4s.http.Http4sSyntax._
import org.http4s.client.Client
import org.http4s.client.middleware.Logger

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.Duration

class HttpClient[F[_]: ConcurrentEffect](connTimeout: Duration)(implicit ec: ExecutionContext) {

  val urls: GithubAPIv3Config = GithubAPIv3Config()

  def get[Res: Decoder](
      accessToken: Option[String] = None,
      method: String,
      headers: Map[String, String] = Map.empty,
      params: Map[String, String] = Map.empty,
      pagination: Option[Pagination] = None
  ): F[GHResponse[Res]] =
    run[Unit, Res](
      RequestBuilder(url = buildURL(method))
        .withAuth(accessToken)
        .withHeaders(headers)
        .withParams(
          params ++ pagination.fold(Map.empty[String, String])(p =>
            Map("page" -> p.page.toString, "per_page" -> p.per_page.toString)
          )
        )
    )

  def patch[Req: Encoder, Res: Decoder](
      accessToken: Option[String] = None,
      method: String,
      headers: Map[String, String] = Map.empty,
      data: Req
  ): F[GHResponse[Res]] =
    run[Req, Res](
      RequestBuilder(buildURL(method)).patchMethod
        .withAuth(accessToken)
        .withHeaders(headers)
        .withData(data)
    )

  def put[Req: Encoder, Res: Decoder](
      accessToken: Option[String] = None,
      url: String,
      headers: Map[String, String] = Map(),
      data: Req
  ): F[GHResponse[Res]] =
    run[Req, Res](
      RequestBuilder(buildURL(url)).putMethod
        .withAuth(accessToken)
        .withHeaders(headers)
        .withData(data)
    )

  def post[Req: Encoder, Res: Decoder](
      accessToken: Option[String] = None,
      url: String,
      headers: Map[String, String] = Map.empty,
      data: Req
  ): F[GHResponse[Res]] =
    run[Req, Res](
      RequestBuilder(buildURL(url)).postMethod
        .withAuth(accessToken)
        .withHeaders(headers)
        .withData(data)
    )

  def postAuth[Req: Encoder, Res: Decoder](
      method: String,
      headers: Map[String, String] = Map.empty,
      data: Req
  ): F[GHResponse[Res]] =
    run[Req, Res](RequestBuilder(buildURL(method)).postMethod.withHeaders(headers).withData(data))

  def postOAuth[Req: Encoder, Res: Decoder](
      url: String,
      headers: Map[String, String] = Map.empty,
      data: Req
  ): F[GHResponse[Res]] =
    run[Req, Res](
      RequestBuilder(url).postMethod
        .withHeaders(Map("Accept" -> "application/json") ++ headers)
        .withData(data)
    )

  def delete(
      accessToken: Option[String] = None,
      method: String,
      headers: Map[String, String] = Map.empty
  ): F[GHResponse[Unit]] =
    run[Unit, Unit](
      RequestBuilder(buildURL(method)).deleteMethod.withHeaders(headers).withAuth(accessToken)
    )

  def deleteWithResponse[Res: Decoder](
      accessToken: Option[String] = None,
      url: String,
      headers: Map[String, String] = Map.empty
  ): F[GHResponse[Res]] =
    run[Unit, Res](
      RequestBuilder(buildURL(url)).deleteMethod
        .withAuth(accessToken)
        .withHeaders(headers)
    )

  val defaultPagination   = Pagination(1, 1000)
  val defaultPage: Int    = 1
  val defaultPerPage: Int = 30
  val resource: Resource[F, Client[F]] = BlazeClientBuilder[F](ec)
    .withConnectTimeout(connTimeout)
    .resource

  private def buildURL(method: String): String = urls.baseUrl + method

  private def run[Req: Encoder, Res: Decoder](request: RequestBuilder[Req]): F[GHResponse[Res]] =
    resource
      .use(
        _.run(
          Request[F]()
            .withMethod(request.httpVerb)
            .withUri(request.toUri(urls))
            .withHeaders(request.toHeaderList: _*)
            .withJsonBody(request.data)
        ).use(response =>
          response
            .attemptAs[Res]
            .value
            .map { e =>
              GHResponse(
                e.leftMap(e => JsonParsingException(e.message, request.data.toString)),
                response.status.code,
                response.headers.toMap
              )
            }
        )
      )

}
