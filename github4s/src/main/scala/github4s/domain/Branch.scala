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

package github4s.domain

case class BranchProtection(
    required_status_checks: Option[RequiredStatusChecks],
    enforce_admins: Boolean,
    required_pull_request_reviews: Option[RequiredPullRequestReviews],
    restrictions: Option[Restrictions],
    required_linear_history: Boolean,
    allow_force_pushes: Boolean,
    allow_deletions: Boolean
)

case class RequiredStatusChecks(
    strict: Boolean,
    contexts: List[String]
)

case class RequiredPullRequestReviews(
    dismissal_restrictions: DismissalRestrictions,
    dismiss_stale_reviews: Boolean,
    require_code_owner_reviews: Boolean,
    required_approving_review_count: Option[Int]
)

case class DismissalRestrictions(
    users: List[String],
    teams: List[String]
)

case class Restrictions(
    users: List[String],
    teams: List[String],
    apps: List[String]
)
