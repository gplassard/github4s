---
layout: docs
title: Project API
permalink: project
---

# Project API

Note: The Projects API is currently available for developers to preview. During the preview period,
the API may change without advance notice. Please see the blog post for full details. To access the
API during the preview period, you must provide a custom media type in the `Accept` header:
 `application/vnd.github.inertia-preview+json`

Github4s supports the [Project API](https://developer.github.com/v3/projects/). As a result,
with Github4s, you can interact with:

- [Project](#project)
  - [List repository projects](#list-repository-projects)
  - [List projects](#list-projects)
  - [Columns](#columns)
    - [List project columns](#list-project-columns)

The following examples assume the following imports and token:

```scala mdoc:silent
import github4s.Github
import github4s.GithubIOSyntax._
import cats.effect.IO
import scala.concurrent.ExecutionContext.Implicits.global

implicit val IOContextShift = IO.contextShift(global)
val accessToken = sys.env.get("GITHUB4S_ACCESS_TOKEN")
```

They also make use of `cats.effect.IO`, but any type container `F` implementing `ConcurrentEffect` will do.

LiftIO syntax for `cats.Id` and `Future` are provided in `GithubIOSyntax`.

## Project

### List repository projects

You can list the projects for a particular repository with `listProjectsRepository`; it takes as arguments:

- `owner`: name of the owner for which we want to retrieve the projects.
- `repo`: name of the repository for which we want to retrieve the projects.
- `state`: filter projects returned by their state. Can be either `open`, `closed`, `all`. Default: `open`, optional
- `pagination`: Limit and Offset for pagination, optional.
- `header`: headers to include in the request, optional.

To list the projects for owner `47deg` and repository `github4s`:

```scala mdoc:compile-only
val listProjectsRepository = Github[IO](accessToken).projects.listProjectsRepository(
    owner = "47deg",
    repo = "github4s",
    headers = Map("Accept" -> "application/vnd.github.inertia-preview+json"))
val response = listProjectsRepository.unsafeRunSync()
response.result match {
  case Left(e) => println(s"Something went wrong: ${e.getMessage}")
  case Right(r) => println(r)
}
```

The `result` on the right is the corresponding [List[Project]][project-scala].

See [the API doc](https://developer.github.com/v3/projects/#list-repository-projects) for full reference.

[project-scala]: https://github.com/47deg/github4s/blob/master/github4s/src/main/scala/github4s/domain/Project.scala


### List projects

You can list the project for a particular organization with `listProjects`; it takes as arguments:

- `org`: name of the organization for which we want to retrieve the projects.
- `state`: filter projects returned by their state. Can be either `open`, `closed`, `all`. Default: `open`, optional
- `pagination`: Limit and Offset for pagination, optional.
- `header`: headers to include in the request, optional.

To list the projects for organization `47deg`:

```scala mdoc:compile-only
val listProjects = Github[IO](accessToken).projects.listProjects(
    org = "47deg",
    headers = Map("Accept" -> "application/vnd.github.inertia-preview+json"))
val response = listProjects.unsafeRunSync()
response.result match {
  case Left(e) => println(s"Something went wrong: ${e.getMessage}")
  case Right(r) => println(r)
}
```

The `result` on the right is the corresponding [List[Project]][project-scala].

See [the API doc](https://developer.github.com/v3/projects/#list-organization-projects) for full reference.

[project-scala]: https://github.com/47deg/github4s/blob/master/github4s/src/main/scala/github4s/domain/Project.scala

### Columns

#### List project columns

You can list the columns for a particular project with `listColumns`; it takes as arguments:

- `project_id`: project id for which we want to retrieve the columns.
- `pagination`: Limit and Offset for pagination, optional.
- `header`: headers to include in the request, optional.

To list the columns for project_id `1910444`:

```scala mdoc:compile-only
val listColumns = Github[IO](accessToken).projects.listColumns(
    project_id = 1910444,
    headers = Map("Accept" -> "application/vnd.github.inertia-preview+json"))
val response = listColumns.unsafeRunSync()
response.result match {
  case Left(e) => println(s"Something went wrong: ${e.getMessage}")
  case Right(r) => println(r)
}
```

The `result` on the right is the corresponding [List[Column]][column-scala].

See [the API doc](https://developer.github.com/v3/projects/columns/#list-project-columns) for full reference.

[column-scala]: https://github.com/47deg/github4s/blob/master/github4s/src/main/scala/github4s/domain/Column.scala
