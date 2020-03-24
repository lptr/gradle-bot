package org.gradle.bot.model

import com.fasterxml.jackson.annotation.JsonProperty

interface GitHubEvent {
    var repository: Repository
    var organization: Organization
    var sender: User
}

data class Committer(
        @JsonProperty("date")
        var date: String,
        @JsonProperty("email")
        var email: String,
        @JsonProperty("name")
        var name: String
)

data class User(
        @JsonProperty("avatar_url")
        var avatarUrl: String,
        @JsonProperty("events_url")
        var eventsUrl: String,
        @JsonProperty("followers_url")
        var followersUrl: String,
        @JsonProperty("following_url")
        var followingUrl: String,
        @JsonProperty("gists_url")
        var gistsUrl: String,
        @JsonProperty("gravatar_id")
        var gravatarId: String,
        @JsonProperty("html_url")
        var htmlUrl: String,
        @JsonProperty("id")
        var id: Int,
        @JsonProperty("login")
        var login: String,
        @JsonProperty("node_id")
        var nodeId: String,
        @JsonProperty("organizations_url")
        var organizationsUrl: String,
        @JsonProperty("received_events_url")
        var receivedEventsUrl: String,
        @JsonProperty("repos_url")
        var reposUrl: String,
        @JsonProperty("site_admin")
        var siteAdmin: Boolean,
        @JsonProperty("starred_url")
        var starredUrl: String,
        @JsonProperty("subscriptions_url")
        var subscriptionsUrl: String,
        @JsonProperty("type")
        var type: String,
        @JsonProperty("url")
        var url: String
)

data class Organization(
        @JsonProperty("avatar_url")
        var avatarUrl: String,
        @JsonProperty("description")
        var description: String,
        @JsonProperty("events_url")
        var eventsUrl: String,
        @JsonProperty("hooks_url")
        var hooksUrl: String,
        @JsonProperty("id")
        var id: Int,
        @JsonProperty("issues_url")
        var issuesUrl: String,
        @JsonProperty("login")
        var login: String,
        @JsonProperty("members_url")
        var membersUrl: String,
        @JsonProperty("node_id")
        var nodeId: String,
        @JsonProperty("public_members_url")
        var publicMembersUrl: String,
        @JsonProperty("repos_url")
        var reposUrl: String,
        @JsonProperty("url")
        var url: String
)

data class Repository(
        @JsonProperty("archive_url")
        var archiveUrl: String,
        @JsonProperty("archived")
        var archived: Boolean,
        @JsonProperty("assignees_url")
        var assigneesUrl: String,
        @JsonProperty("blobs_url")
        var blobsUrl: String,
        @JsonProperty("branches_url")
        var branchesUrl: String,
        @JsonProperty("clone_url")
        var cloneUrl: String,
        @JsonProperty("collaborators_url")
        var collaboratorsUrl: String,
        @JsonProperty("comments_url")
        var commentsUrl: String,
        @JsonProperty("commits_url")
        var commitsUrl: String,
        @JsonProperty("compare_url")
        var compareUrl: String,
        @JsonProperty("contents_url")
        var contentsUrl: String,
        @JsonProperty("contributors_url")
        var contributorsUrl: String,
        @JsonProperty("created_at")
        var createdAt: String,
        @JsonProperty("default_branch")
        var defaultBranch: String,
        @JsonProperty("deployments_url")
        var deploymentsUrl: String,
        @JsonProperty("description")
        var description: String,
        @JsonProperty("disabled")
        var disabled: Boolean,
        @JsonProperty("downloads_url")
        var downloadsUrl: String,
        @JsonProperty("events_url")
        var eventsUrl: String,
        @JsonProperty("fork")
        var fork: Boolean,
        @JsonProperty("forks")
        var forks: Int,
        @JsonProperty("forks_count")
        var forksCount: Int,
        @JsonProperty("forks_url")
        var forksUrl: String,
        @JsonProperty("full_name")
        var fullName: String,
        @JsonProperty("git_commits_url")
        var gitCommitsUrl: String,
        @JsonProperty("git_refs_url")
        var gitRefsUrl: String,
        @JsonProperty("git_tags_url")
        var gitTagsUrl: String,
        @JsonProperty("git_url")
        var gitUrl: String,
        @JsonProperty("has_downloads")
        var hasDownloads: Boolean,
        @JsonProperty("has_issues")
        var hasIssues: Boolean,
        @JsonProperty("has_pages")
        var hasPages: Boolean,
        @JsonProperty("has_projects")
        var hasProjects: Boolean,
        @JsonProperty("has_wiki")
        var hasWiki: Boolean,
        @JsonProperty("homepage")
        var homepage: Any?,
        @JsonProperty("hooks_url")
        var hooksUrl: String,
        @JsonProperty("html_url")
        var htmlUrl: String,
        @JsonProperty("id")
        var id: Int,
        @JsonProperty("issue_comment_url")
        var issueCommentUrl: String,
        @JsonProperty("issue_events_url")
        var issueEventsUrl: String,
        @JsonProperty("issues_url")
        var issuesUrl: String,
        @JsonProperty("keys_url")
        var keysUrl: String,
        @JsonProperty("labels_url")
        var labelsUrl: String,
        @JsonProperty("language")
        var language: String,
        @JsonProperty("languages_url")
        var languagesUrl: String,
        @JsonProperty("license")
        var license: Any?,
        @JsonProperty("merges_url")
        var mergesUrl: String,
        @JsonProperty("milestones_url")
        var milestonesUrl: String,
        @JsonProperty("mirror_url")
        var mirrorUrl: Any?,
        @JsonProperty("name")
        var name: String,
        @JsonProperty("node_id")
        var nodeId: String,
        @JsonProperty("notifications_url")
        var notificationsUrl: String,
        @JsonProperty("open_issues")
        var openIssues: Int,
        @JsonProperty("open_issues_count")
        var openIssuesCount: Int,
        @JsonProperty("owner")
        var owner: User,
        @JsonProperty("private")
        var `private`: Boolean,
        @JsonProperty("pulls_url")
        var pullsUrl: String,
        @JsonProperty("pushed_at")
        var pushedAt: String,
        @JsonProperty("releases_url")
        var releasesUrl: String,
        @JsonProperty("size")
        var size: Int,
        @JsonProperty("ssh_url")
        var sshUrl: String,
        @JsonProperty("stargazers_count")
        var stargazersCount: Int,
        @JsonProperty("stargazers_url")
        var stargazersUrl: String,
        @JsonProperty("statuses_url")
        var statusesUrl: String,
        @JsonProperty("subscribers_url")
        var subscribersUrl: String,
        @JsonProperty("subscription_url")
        var subscriptionUrl: String,
        @JsonProperty("svn_url")
        var svnUrl: String,
        @JsonProperty("tags_url")
        var tagsUrl: String,
        @JsonProperty("teams_url")
        var teamsUrl: String,
        @JsonProperty("trees_url")
        var treesUrl: String,
        @JsonProperty("updated_at")
        var updatedAt: String,
        @JsonProperty("url")
        var url: String,
        @JsonProperty("watchers")
        var watchers: Int,
        @JsonProperty("watchers_count")
        var watchersCount: Int
)