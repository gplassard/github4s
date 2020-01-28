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

import java.util.concurrent.TimeUnit

import cats.effect.{ConcurrentEffect, ContextShift, IO, Timer}
import github4s.taglessFinal.domain.Pagination
import io.circe.Decoder
import github4s.GithubResponses.{
  GHException,
  GHResponse,
  GHResult,
  JsonParsingException,
  UnsuccessfulHttpRequest
}
import cats.implicits._
import org.http4s._
import org.http4s.client.blaze._
import org.http4s.MediaType
import org.http4s.circe.CirceEntityDecoder._
import org.http4s.headers.`Content-Type`
import scala.concurrent.duration.Duration

class HttpExec[M[_]: ConcurrentEffect](implicit u: GithubApiUrls) {

  def run[A](rb: HttpRequestBuilder[M])(implicit D: Decoder[A]): M[GHResponse[A]] =
    runMap[A](rb, decodeEntity[A])

  def runEmpty(rb: HttpRequestBuilder[M]): M[GHResponse[Unit]] =
    runMap[Unit](rb, emptyResponse)

  private[this] def runMap[A](
      rb: HttpRequestBuilder[M],
      mapResponse: Response[M] => M[GHResponse[A]])(implicit D: Decoder[A]): M[GHResponse[A]] = {

    val connTimeout: Duration = Duration(1000l, TimeUnit.MILLISECONDS)
    val readTimeoutMs: Int    = 5000 ///TODO where to add this parameter to client?

    //TODO propogate proper header types instead of doing this conversion? Same for params.
    val headerList: List[Header] = (rb.headers.map({ kv =>
      Header(kv._1, kv._2)
    }) ++ rb.authHeader.map({ kv =>
      Header(kv._1, kv._2)
    })).toList

    val request: Request[M] = {
      val req = Request[M]()
        .withMethod(rb.httpVerb)
        .withUri(
          Uri.fromString(rb.url).getOrElse(uri"https://api.github.com/") =? (rb.params.map(kv =>
            (kv._1, List(kv._2))))) //TODO handle url parsing errors without defaulting
        .withHeaders(headerList: _*)

      rb.data match {
        case Some(d) =>
          req.withContentType(`Content-Type`(MediaType.application.json)).withEntity(d)
        case _ => req
      }
    }

    // TODO BlazeClient requires F: ConcurrentEffect which requries F:ContextShift. How to handle case where F != IO?
    import scala.concurrent.ExecutionContext.Implicits.global
    implicit val timer: Timer[IO]     = cats.effect.IO.timer(global)
    implicit val cs: ContextShift[IO] = cats.effect.IO.contextShift(global)

    BlazeClientBuilder[M](global)
      .withConnectTimeout(connTimeout)
      .resource
      .use({ client =>
        client
          .expect[A](request)
          .flatMap({ a =>
            Either.right[GHException, GHResult[A]](GHResult(a, 200, Map())).pure[M]
          }) //TODO cant get response headers?
      //client.fetch(request)(mapResponse) // TODO Chose: fetch with mapResponse refactored to parse streams OR client.expect[A]
      })
  }

  def toEntity[A](
      response: Response[M],
      mapReponse: (Response[M] => M[GHResponse[A]])): M[GHResponse[A]] =
    response match {
      case r if r.status.code == 200 =>
        mapReponse(r)
      case r =>
        Either
          .left[GHException, GHResult[A]](
            UnsuccessfulHttpRequest(
              s"Failed invoking with status : ${r.status} body : \n ${r.body.toString()}",
              r.status.code
            )
          )
          .pure[M]
    }

  def emptyResponse(r: Response[M]): M[GHResponse[Unit]] =
    Either
      .right[GHException, GHResult[Unit]](
        GHResult((): Unit, r.status.code, headersToMap(r.headers)))
      .pure[M]

  def decodeEntity[A](r: Response[M])(implicit D: Decoder[A]): M[GHResponse[A]] = ???
  /*parse(r.body.toString())
      .flatMap(_.as[A])
      .bimap[GHException, GHResult[A]](
        e => JsonParsingException(e.getMessage, r.body.toString()),
        result => GHResult(result, r.status.code, headersToMap(r.headers))
      )
      .pure[M]*/ //

  private def headersToMap(headers: Headers): Map[String, String] =
    headers.toList
      .map({ h =>
        (h.name.toString().toLowerCase(), h.value)
      })
      .toMap

  private def toLowerCase(
      headers: Map[String, IndexedSeq[String]]): Map[String, IndexedSeq[String]] =
    headers.map(e => (e._1.toLowerCase, e._2))
}

class HttpRequestBuilder[M[_]](
    val url: String,
    val httpVerb: Method = Method.GET,
    val authHeader: Map[String, String] = Map.empty[String, String],
    val data: Option[String] = None,
    val params: Map[String, String] = Map.empty[String, String],
    val headers: Map[String, String] = Map.empty[String, String]
) {

  def postMethod = new HttpRequestBuilder[M](url, Method.POST, authHeader, data, params, headers)

  def patchMethod = new HttpRequestBuilder[M](url, Method.PATCH, authHeader, data, params, headers)

  def putMethod = new HttpRequestBuilder[M](url, Method.PUT, authHeader, data, params, headers)

  def deleteMethod =
    new HttpRequestBuilder[M](url, Method.DELETE, authHeader, data, params, headers)

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
      httpVerb: Method = Method.GET,
      authHeader: Map[String, String] = Map.empty[String, String],
      data: Option[String] = None,
      params: Map[String, String] = Map.empty[String, String],
      headers: Map[String, String] = Map.empty[String, String]
  ) = new HttpRequestBuilder[M](url, httpVerb, authHeader, data, params, headers)
}

class HttpClient[M[_]: ConcurrentEffect](implicit urls: GithubApiUrls) {
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
