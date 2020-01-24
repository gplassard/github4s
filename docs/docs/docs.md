---
layout: docs
title: Getting Started
permalink: docs
---

# Getting started

## API token

In order to access the Github API, you will need to have [an access token][access-token] with the
appropriate scopes (i.e. if you want to create gists, your token will need to have the gist scope).

## Github4s

First things first, we'll need to import `github4s.Github` which is the entry point to the Github
API in Github4s.

```scala mdoc:silent
import github4s.Github
import github4s.Github._
```

In order for Github4s to work, you'll need the appropriate implicits in your scope:

```scala
import github4s.implicits._
```

```scala mdoc:invisible
val accessToken = sys.env.get("GITHUB4S_ACCESS_TOKEN")
```

Every Github4s API call returns a `GHIO[GHResponse[A]]` which is an alias for
`Free[Github4s, GHResponse[A]]`.

`GHResponse[A]` is, in turn, a type alias for `Either[GHException, GHResult[A]]`.

`GHResult` contains the result `A` given by Github as well as the status code and headers of the
response:

```scala
case class GHResult[A](result: A, statusCode: Int, headers: Map[String, IndexedSeq[String]])
```

As an introductory example, we can get a user with the following:

```scala mdoc:silent
val user1 = Github(accessToken).users.get("rafaparadela")
```

`user1` in this case is a `GHIO[GHResponse[User]]` which we can run (`foldMap`) with
`exec[M[_]]` where `M[_]` that represents any type container that implements
`MonadError[M, Throwable]` (for instance `cats.Eval`).

A few examples follow with different `MonadError[M, Throwable]`.

### Using `cats.Eval`

```scala mdoc:silent
object ProgramEval {
  import github4s.implicits._
  val u1 = user1.exec[cats.Eval]().value
}
```

As mentioned above, `u1` should have an `GHResult[User]` in the right.

```scala mdoc:silent
import github4s.GithubResponses.GHResult
ProgramEval.u1 match {
  case Right(GHResult(result, status@_, headers@_)) => result.login
  case Left(e) => e.getMessage
}
```

### Using `cats.Id`

```scala mdoc:silent
object ProgramId {
  import github4s.implicits._
  val u2 = Github(accessToken).users.get("raulraja").exec[cats.Id]()
}
```

### Using `Future`

```scala mdoc:silent
object ProgramFuture {
  import scala.concurrent.ExecutionContext.Implicits.global
  import scala.concurrent.duration._
  import scala.concurrent.Await
  import github4s.implicits._

  // execFuture is a shortcut for exec[Future]
  val u3 = Github(accessToken).users.get("dialelo").execFuture()
  Await.result(u3, 2.seconds)
}
```

### Using `cats.effect.Sync`

We can use any `cats.effect.Sync`, here we're using `cats.effect.IO`:

```scala mdoc:silent
object ProgramSync {
  import cats.effect.IO
  import github4s.cats.effect.implicits._

  val u5 = Github(accessToken).users.get("juanpedromoreno").exec[IO]()
  u5.unsafeRunSync
}
```

Note that you'll need a dependency to the `github4s-cats-effect` package to leverage the
cats-effect integration.

## Specifying custom headers

The different `exec` methods let users include custom headers for any Github API request:

```scala mdoc:silent
object ProgramEvalWithHeaders {
  import github4s.implicits._
  val userHeaders = Map("user-agent" -> "github4s")
  val user1 = Github(accessToken).users.get("rafaparadela").exec[cats.Eval](userHeaders).value
}
```

[http-client]: https://github.com/47deg/github4s/blob/master/github4s/shared/src/main/scala/github4s/HttpClient.scala
[scalaj]: https://github.com/scalaj/scalaj-http
[access-token]: https://github.com/settings/tokens
