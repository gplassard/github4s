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

package github4s

import cats.Applicative
import github4s.free.domain.Pagination
import io.circe.Decoder
import github4s.GithubResponses.{
  GHResponse,
  GHResult,
  JsonParsingException,
  UnsuccessfulHttpRequest
}
import github4s.HttpClient._
import io.circe.jackson.parse
import scalaj.http.{Http, HttpOptions, HttpRequest, HttpResponse}
import cats.implicits._

object HttpClient {
  type Headers = Map[String, String]

  sealed trait HttpVerb {
    def verb: String
  }

  case object Get extends HttpVerb {
    def verb = "GET"
  }

  case object Post extends HttpVerb {
    def verb = "POST"
  }

  case object Put extends HttpVerb {
    def verb = "PUT"
  }

  case object Delete extends HttpVerb {
    def verb = "DELETE"
  }

  case object Patch extends HttpVerb {
    def verb = "PATCH"
  }

  sealed trait HttpStatus {
    def statusCode: Int
  }

  case object HttpCode200 extends HttpStatus {
    def statusCode = 200
  }

  case object HttpCode299 extends HttpStatus {
    def statusCode = 299
  }
}

class HttpExec[M[_]: Applicative] {
  def run[A](rb: HttpRequestBuilder[M])(implicit D: Decoder[A]): M[GHResponse[A]] =
    runMap[A](rb, decodeEntity[A])

  def runEmpty(rb: HttpRequestBuilder[M]): M[GHResponse[Unit]] =
    runMap[Unit](rb, emptyResponse)

  private[this] def runMap[A](
      rb: HttpRequestBuilder[M],
      mapResponse: HttpResponse[String] => GHResponse[A]): M[GHResponse[A]] = {

    val connTimeoutMs: Int = 1000
    val readTimeoutMs: Int = 5000

    val params = rb.params.map {
      case (key, value) => s"$key=$value"
    } mkString ("?", "&", "")

    val request = Http(url = rb.url)
      .method(rb.httpVerb.verb)
      .option(HttpOptions.connTimeout(connTimeoutMs))
      .option(HttpOptions.readTimeout(readTimeoutMs))
      .params(rb.params)
      .headers(rb.authHeader)
      .headers(rb.headers)
      .copy(urlBuilder = (req: HttpRequest) => s"${req.url}$params")
    rb.data match {
      case Some(d) =>
        (
          toEntity[A](
            request
              .postData(d)
              .method(rb.httpVerb.verb)
              .header("content-type", "application/json")
              .asString,
            mapResponse)
        ).pure[M]
      case _ => (toEntity[A](request.asString, mapResponse).pure[M])
    }
  }

  def toEntity[A](
      response: HttpResponse[String],
      mapResponse: (HttpResponse[String]) => GHResponse[A]): GHResponse[A] =
    response match {
      case r if r.isSuccess =>
        mapResponse(r)
      case r =>
        Either.left(
          UnsuccessfulHttpRequest(
            s"Failed invoking with status : ${r.code} body : \n ${r.body}",
            r.code
          )
        )
    }

  def emptyResponse(r: HttpResponse[String]): GHResponse[Unit] =
    Either.right(GHResult((): Unit, r.code, toLowerCase(r.headers)))

  def decodeEntity[A](r: HttpResponse[String])(implicit D: Decoder[A]): GHResponse[A] =
    parse(r.body)
      .flatMap(_.as[A])
      .bimap(
        e => JsonParsingException(e.getMessage, r.body),
        result => GHResult(result, r.code, toLowerCase(r.headers))
      )

  private def toLowerCase(
      headers: Map[String, IndexedSeq[String]]): Map[String, IndexedSeq[String]] =
    headers.map(e => (e._1.toLowerCase, e._2))
}

class HttpRequestBuilder[M[_]](
    val url: String,
    val httpVerb: HttpVerb = Get,
    val authHeader: Map[String, String] = Map.empty[String, String],
    val data: Option[String] = None,
    val params: Map[String, String] = Map.empty[String, String],
    val headers: Map[String, String] = Map.empty[String, String]
) {

  def postMethod = new HttpRequestBuilder[M](url, Post, authHeader, data, params, headers)

  def patchMethod = new HttpRequestBuilder[M](url, Patch, authHeader, data, params, headers)

  def putMethod = new HttpRequestBuilder[M](url, Put, authHeader, data, params, headers)

  def deleteMethod = new HttpRequestBuilder[M](url, Delete, authHeader, data, params, headers)

  def withAuth(accessToken: Option[String] = None) = {
    val authHeader = accessToken match {
      case Some(token) => Map("Authorization" -> s"token $token")
      case _           => Map.empty[String, String]
    }
    new HttpRequestBuilder[M](url, httpVerb, authHeader, data, params, headers)
  }

  def withHeaders(headers: Map[String, String]) =
    new HttpRequestBuilder[M](url, httpVerb, authHeader, data, params, headers)

  def withParams(params: Map[String, String]) =
    new HttpRequestBuilder[M](url, httpVerb, authHeader, data, params, headers)

  def withData(data: String) =
    new HttpRequestBuilder[M](url, httpVerb, authHeader, Option(data), params, headers)
}

object HttpRequestBuilder {
  def httpRequestBuilder[M[_]](
      url: String,
      httpVerb: HttpVerb = Get,
      authHeader: Map[String, String] = Map.empty[String, String],
      data: Option[String] = None,
      params: Map[String, String] = Map.empty[String, String],
      headers: Map[String, String] = Map.empty[String, String]
  ) = new HttpRequestBuilder[M](url, httpVerb, authHeader, data, params, headers)
}

class HttpClient[M[_]: Applicative](implicit urls: GithubApiUrls) {
  import HttpRequestBuilder._

  val defaultPagination = Pagination(1, 1000)
  val httpExec          = new HttpExec[M]

  def get[A](
      accessToken: Option[String] = None,
      method: String,
      headers: Map[String, String] = Map.empty,
      params: Map[String, String] = Map.empty,
      pagination: Option[Pagination] = None
  )(implicit D: Decoder[A]): M[GHResponse[A]] =
    httpExec.run[A](
      httpRequestBuilder(buildURL(method))
        .withAuth(accessToken)
        .withHeaders(headers)
        .withParams(params ++ pagination.fold(Map.empty[String, String])(p =>
          Map("page" -> p.page.toString, "per_page" -> p.per_page.toString))))

  def patch[A](
      accessToken: Option[String] = None,
      method: String,
      headers: Map[String, String] = Map.empty,
      data: String)(implicit D: Decoder[A]): M[GHResponse[A]] =
    httpExec.run[A](
      httpRequestBuilder(buildURL(method)).patchMethod
        .withAuth(accessToken)
        .withHeaders(headers)
        .withData(data))

  def put[A](
      accessToken: Option[String] = None,
      url: String,
      headers: Map[String, String] = Map(),
      data: String)(implicit D: Decoder[A]): M[GHResponse[A]] =
    httpExec.run[A](
      httpRequestBuilder(buildURL(url)).putMethod
        .withAuth(accessToken)
        .withHeaders(headers)
        .withData(data))

  def post[A](
      accessToken: Option[String] = None,
      url: String,
      headers: Map[String, String] = Map.empty,
      data: String
  )(implicit D: Decoder[A]): M[GHResponse[A]] =
    httpExec.run[A](
      httpRequestBuilder(buildURL(url)).postMethod
        .withAuth(accessToken)
        .withHeaders(headers)
        .withData(data))

  def postAuth[A](
      method: String,
      headers: Map[String, String] = Map.empty,
      data: String
  )(implicit D: Decoder[A]): M[GHResponse[A]] =
    httpExec.run[A](
      httpRequestBuilder(buildURL(method)).postMethod.withHeaders(headers).withData(data))

  def postOAuth[A](
      url: String,
      headers: Map[String, String] = Map.empty,
      data: String
  )(implicit D: Decoder[A]): M[GHResponse[A]] =
    httpExec.run[A](
      httpRequestBuilder(url).postMethod
        .withHeaders(Map("Accept" -> "application/json") ++ headers)
        .withData(data))

  def delete(
      accessToken: Option[String] = None,
      method: String,
      headers: Map[String, String] = Map.empty): M[GHResponse[Unit]] =
    httpExec.runEmpty(
      httpRequestBuilder(buildURL(method)).deleteMethod.withHeaders(headers).withAuth(accessToken))

  def deleteWithResponse[A](
      accessToken: Option[String] = None,
      url: String,
      headers: Map[String, String] = Map.empty
  )(implicit D: Decoder[A]): M[GHResponse[A]] =
    httpExec.run[A](
      httpRequestBuilder(buildURL(url)).deleteMethod
        .withAuth(accessToken)
        .withHeaders(headers))

  private def buildURL(method: String) = urls.baseUrl + method

  val defaultPage: Int    = 1
  val defaultPerPage: Int = 30
}
