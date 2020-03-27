package org.gradle.bot.model

import com.fasterxml.jackson.annotation.JsonProperty


/*
Headers
Request URL: http://webhook.yinghekongjian.com:5000/events
Request method: POST
content-type: application/json
Expect:
User-Agent: GitHub-Hookshot/6c4e595
X-GitHub-Delivery: 6b7f4b00-6d7f-11ea-9098-546b0b7b8010
X-GitHub-Event: issue_comment
Payload
{
  "action": "created",
  "issue": {
    "url": "https://api.github.com/repos/hcsp/save-pull-requests-to-csv/issues/99",
    "repository_url": "https://api.github.com/repos/hcsp/save-pull-requests-to-csv",
    "labels_url": "https://api.github.com/repos/hcsp/save-pull-requests-to-csv/issues/99/labels{/name}",
    "comments_url": "https://api.github.com/repos/hcsp/save-pull-requests-to-csv/issues/99/comments",
    "events_url": "https://api.github.com/repos/hcsp/save-pull-requests-to-csv/issues/99/events",
    "html_url": "https://github.com/hcsp/save-pull-requests-to-csv/pull/99",
    "id": 585724164,
    "node_id": "MDExOlB1bGxSZXF1ZXN0MzkyMDA5NzAx",
    "number": 99,
    "title": "爬取GitHub的Pull request并存储为CSV文件",
    "user": {
      "login": "Truthcode",
      "id": 61576871,
      "node_id": "MDQ6VXNlcjYxNTc2ODcx",
      "avatar_url": "https://avatars1.githubusercontent.com/u/61576871?v=4",
      "gravatar_id": "",
      "url": "https://api.github.com/users/Truthcode",
      "html_url": "https://github.com/Truthcode",
      "followers_url": "https://api.github.com/users/Truthcode/followers",
      "following_url": "https://api.github.com/users/Truthcode/following{/other_user}",
      "gists_url": "https://api.github.com/users/Truthcode/gists{/gist_id}",
      "starred_url": "https://api.github.com/users/Truthcode/starred{/owner}{/repo}",
      "subscriptions_url": "https://api.github.com/users/Truthcode/subscriptions",
      "organizations_url": "https://api.github.com/users/Truthcode/orgs",
      "repos_url": "https://api.github.com/users/Truthcode/repos",
      "events_url": "https://api.github.com/users/Truthcode/events{/privacy}",
      "received_events_url": "https://api.github.com/users/Truthcode/received_events",
      "type": "User",
      "site_admin": false
    },
    "labels": [
      {
        "id": 1554353896,
        "node_id": "MDU6TGFiZWwxNTU0MzUzODk2",
        "url": "https://api.github.com/repos/hcsp/save-pull-requests-to-csv/labels/from:VIP",
        "name": "from:VIP",
        "color": "3b5dab",
        "default": false,
        "description": null
      },
      {
        "id": 1554353906,
        "node_id": "MDU6TGFiZWwxNTU0MzUzOTA2",
        "url": "https://api.github.com/repos/hcsp/save-pull-requests-to-csv/labels/is:answer",
        "name": "is:answer",
        "color": "008672",
        "default": false,
        "description": null
      }
    ],
    "state": "closed",
    "locked": false,
    "assignee": null,
    "assignees": [

    ],
    "milestone": null,
    "comments": 6,
    "created_at": "2020-03-22T13:56:37Z",
    "updated_at": "2020-03-24T03:27:42Z",
    "closed_at": "2020-03-24T02:36:58Z",
    "author_association": "CONTRIBUTOR",
    "pull_request": {
      "url": "https://api.github.com/repos/hcsp/save-pull-requests-to-csv/pulls/99",
      "html_url": "https://github.com/hcsp/save-pull-requests-to-csv/pull/99",
      "diff_url": "https://github.com/hcsp/save-pull-requests-to-csv/pull/99.diff",
      "patch_url": "https://github.com/hcsp/save-pull-requests-to-csv/pull/99.patch"
    },
    "body": "<!--- 你要打开的这个Pull request(PR)的类型是？默认是题目解答，如果你正在修复当前的仓库的缺陷，请选择对应的类型 -->\r\n\r\n- [x] 这个PR解答了当前仓库中的题目（机器人会自动判题并合并当前PR）\r\n- [ ] 这个PR修复了当前仓库中的一些代码缺陷（机器人不会判题，而是由管理员来处理当前PR）\r\n\r\n"
  },
  "comment": {
    "url": "https://api.github.com/repos/hcsp/save-pull-requests-to-csv/issues/comments/602991898",
    "html_url": "https://github.com/hcsp/save-pull-requests-to-csv/pull/99#issuecomment-602991898",
    "issue_url": "https://api.github.com/repos/hcsp/save-pull-requests-to-csv/issues/99",
    "id": 602991898,
    "node_id": "MDEyOklzc3VlQ29tbWVudDYwMjk5MTg5OA==",
    "user": {
      "login": "blindpirate",
      "id": 12689835,
      "node_id": "MDQ6VXNlcjEyNjg5ODM1",
      "avatar_url": "https://avatars3.githubusercontent.com/u/12689835?v=4",
      "gravatar_id": "",
      "url": "https://api.github.com/users/blindpirate",
      "html_url": "https://github.com/blindpirate",
      "followers_url": "https://api.github.com/users/blindpirate/followers",
      "following_url": "https://api.github.com/users/blindpirate/following{/other_user}",
      "gists_url": "https://api.github.com/users/blindpirate/gists{/gist_id}",
      "starred_url": "https://api.github.com/users/blindpirate/starred{/owner}{/repo}",
      "subscriptions_url": "https://api.github.com/users/blindpirate/subscriptions",
      "organizations_url": "https://api.github.com/users/blindpirate/orgs",
      "repos_url": "https://api.github.com/users/blindpirate/repos",
      "events_url": "https://api.github.com/users/blindpirate/events{/privacy}",
      "received_events_url": "https://api.github.com/users/blindpirate/received_events",
      "type": "User",
      "site_admin": false
    },
    "created_at": "2020-03-24T03:27:42Z",
    "updated_at": "2020-03-24T03:27:42Z",
    "author_association": "COLLABORATOR",
    "body": "test"
  },
  "repository": {
    "id": 200386039,
    "node_id": "MDEwOlJlcG9zaXRvcnkyMDAzODYwMzk=",
    "name": "save-pull-requests-to-csv",
    "full_name": "hcsp/save-pull-requests-to-csv",
    "private": false,
    "owner": {
      "login": "hcsp",
      "id": 47730780,
      "node_id": "MDEyOk9yZ2FuaXphdGlvbjQ3NzMwNzgw",
      "avatar_url": "https://avatars3.githubusercontent.com/u/47730780?v=4",
      "gravatar_id": "",
      "url": "https://api.github.com/users/hcsp",
      "html_url": "https://github.com/hcsp",
      "followers_url": "https://api.github.com/users/hcsp/followers",
      "following_url": "https://api.github.com/users/hcsp/following{/other_user}",
      "gists_url": "https://api.github.com/users/hcsp/gists{/gist_id}",
      "starred_url": "https://api.github.com/users/hcsp/starred{/owner}{/repo}",
      "subscriptions_url": "https://api.github.com/users/hcsp/subscriptions",
      "organizations_url": "https://api.github.com/users/hcsp/orgs",
      "repos_url": "https://api.github.com/users/hcsp/repos",
      "events_url": "https://api.github.com/users/hcsp/events{/privacy}",
      "received_events_url": "https://api.github.com/users/hcsp/received_events",
      "type": "Organization",
      "site_admin": false
    },
    "html_url": "https://github.com/hcsp/save-pull-requests-to-csv",
    "description": "Java basic practice for beginners: IO",
    "fork": false,
    "url": "https://api.github.com/repos/hcsp/save-pull-requests-to-csv",
    "forks_url": "https://api.github.com/repos/hcsp/save-pull-requests-to-csv/forks",
    "keys_url": "https://api.github.com/repos/hcsp/save-pull-requests-to-csv/keys{/key_id}",
    "collaborators_url": "https://api.github.com/repos/hcsp/save-pull-requests-to-csv/collaborators{/collaborator}",
    "teams_url": "https://api.github.com/repos/hcsp/save-pull-requests-to-csv/teams",
    "hooks_url": "https://api.github.com/repos/hcsp/save-pull-requests-to-csv/hooks",
    "issue_events_url": "https://api.github.com/repos/hcsp/save-pull-requests-to-csv/issues/events{/number}",
    "events_url": "https://api.github.com/repos/hcsp/save-pull-requests-to-csv/events",
    "assignees_url": "https://api.github.com/repos/hcsp/save-pull-requests-to-csv/assignees{/user}",
    "branches_url": "https://api.github.com/repos/hcsp/save-pull-requests-to-csv/branches{/branch}",
    "tags_url": "https://api.github.com/repos/hcsp/save-pull-requests-to-csv/tags",
    "blobs_url": "https://api.github.com/repos/hcsp/save-pull-requests-to-csv/git/blobs{/sha}",
    "git_tags_url": "https://api.github.com/repos/hcsp/save-pull-requests-to-csv/git/tags{/sha}",
    "git_refs_url": "https://api.github.com/repos/hcsp/save-pull-requests-to-csv/git/refs{/sha}",
    "trees_url": "https://api.github.com/repos/hcsp/save-pull-requests-to-csv/git/trees{/sha}",
    "statuses_url": "https://api.github.com/repos/hcsp/save-pull-requests-to-csv/statuses/{sha}",
    "languages_url": "https://api.github.com/repos/hcsp/save-pull-requests-to-csv/languages",
    "stargazers_url": "https://api.github.com/repos/hcsp/save-pull-requests-to-csv/stargazers",
    "contributors_url": "https://api.github.com/repos/hcsp/save-pull-requests-to-csv/contributors",
    "subscribers_url": "https://api.github.com/repos/hcsp/save-pull-requests-to-csv/subscribers",
    "subscription_url": "https://api.github.com/repos/hcsp/save-pull-requests-to-csv/subscription",
    "commits_url": "https://api.github.com/repos/hcsp/save-pull-requests-to-csv/commits{/sha}",
    "git_commits_url": "https://api.github.com/repos/hcsp/save-pull-requests-to-csv/git/commits{/sha}",
    "comments_url": "https://api.github.com/repos/hcsp/save-pull-requests-to-csv/comments{/number}",
    "issue_comment_url": "https://api.github.com/repos/hcsp/save-pull-requests-to-csv/issues/comments{/number}",
    "contents_url": "https://api.github.com/repos/hcsp/save-pull-requests-to-csv/contents/{+path}",
    "compare_url": "https://api.github.com/repos/hcsp/save-pull-requests-to-csv/compare/{base}...{head}",
    "merges_url": "https://api.github.com/repos/hcsp/save-pull-requests-to-csv/merges",
    "archive_url": "https://api.github.com/repos/hcsp/save-pull-requests-to-csv/{archive_format}{/ref}",
    "downloads_url": "https://api.github.com/repos/hcsp/save-pull-requests-to-csv/downloads",
    "issues_url": "https://api.github.com/repos/hcsp/save-pull-requests-to-csv/issues{/number}",
    "pulls_url": "https://api.github.com/repos/hcsp/save-pull-requests-to-csv/pulls{/number}",
    "milestones_url": "https://api.github.com/repos/hcsp/save-pull-requests-to-csv/milestones{/number}",
    "notifications_url": "https://api.github.com/repos/hcsp/save-pull-requests-to-csv/notifications{?since,all,participating}",
    "labels_url": "https://api.github.com/repos/hcsp/save-pull-requests-to-csv/labels{/name}",
    "releases_url": "https://api.github.com/repos/hcsp/save-pull-requests-to-csv/releases{/id}",
    "deployments_url": "https://api.github.com/repos/hcsp/save-pull-requests-to-csv/deployments",
    "created_at": "2019-08-03T14:40:20Z",
    "updated_at": "2020-03-24T02:37:04Z",
    "pushed_at": "2020-03-24T02:37:02Z",
    "git_url": "git://github.com/hcsp/save-pull-requests-to-csv.git",
    "ssh_url": "git@github.com:hcsp/save-pull-requests-to-csv.git",
    "clone_url": "https://github.com/hcsp/save-pull-requests-to-csv.git",
    "svn_url": "https://github.com/hcsp/save-pull-requests-to-csv",
    "homepage": null,
    "size": 222,
    "stargazers_count": 0,
    "watchers_count": 0,
    "language": "Java",
    "has_issues": true,
    "has_projects": true,
    "has_downloads": true,
    "has_wiki": true,
    "has_pages": false,
    "forks_count": 28,
    "mirror_url": null,
    "archived": false,
    "disabled": false,
    "open_issues_count": 3,
    "license": null,
    "forks": 28,
    "open_issues": 3,
    "watchers": 0,
    "default_branch": "master"
  },
  "organization": {
    "login": "hcsp",
    "id": 47730780,
    "node_id": "MDEyOk9yZ2FuaXphdGlvbjQ3NzMwNzgw",
    "url": "https://api.github.com/orgs/hcsp",
    "repos_url": "https://api.github.com/orgs/hcsp/repos",
    "events_url": "https://api.github.com/orgs/hcsp/events",
    "hooks_url": "https://api.github.com/orgs/hcsp/hooks",
    "issues_url": "https://api.github.com/orgs/hcsp/issues",
    "members_url": "https://api.github.com/orgs/hcsp/members{/member}",
    "public_members_url": "https://api.github.com/orgs/hcsp/public_members{/member}",
    "avatar_url": "https://avatars3.githubusercontent.com/u/47730780?v=4",
    "description": "A hardcore space."
  },
  "sender": {
    "login": "blindpirate",
    "id": 12689835,
    "node_id": "MDQ6VXNlcjEyNjg5ODM1",
    "avatar_url": "https://avatars3.githubusercontent.com/u/12689835?v=4",
    "gravatar_id": "",
    "url": "https://api.github.com/users/blindpirate",
    "html_url": "https://github.com/blindpirate",
    "followers_url": "https://api.github.com/users/blindpirate/followers",
    "following_url": "https://api.github.com/users/blindpirate/following{/other_user}",
    "gists_url": "https://api.github.com/users/blindpirate/gists{/gist_id}",
    "starred_url": "https://api.github.com/users/blindpirate/starred{/owner}{/repo}",
    "subscriptions_url": "https://api.github.com/users/blindpirate/subscriptions",
    "organizations_url": "https://api.github.com/users/blindpirate/orgs",
    "repos_url": "https://api.github.com/users/blindpirate/repos",
    "events_url": "https://api.github.com/users/blindpirate/events{/privacy}",
    "received_events_url": "https://api.github.com/users/blindpirate/received_events",
    "type": "User",
    "site_admin": false
  }
}
 */

data class IssueCommentEvent(
        @JsonProperty("action")
        var action: String,
        @JsonProperty("comment")
        var comment: IssueComment,
        @JsonProperty("issue")
        var issue: Issue,
        @JsonProperty("organization")
        override var organization: Organization?,
        @JsonProperty("repository")
        override var repository: Repository,
        @JsonProperty("sender")
        override var sender: User
) : GitHubEvent {
    data class Issue(
            @JsonProperty("assignee")
            var assignee: User?,
            @JsonProperty("assignees")
            var assignees: List<User>,
            @JsonProperty("author_association")
            var authorAssociation: AuthorAssociation,
            @JsonProperty("body")
            var body: String,
            @JsonProperty("closed_at")
            var closedAt: String?,
            @JsonProperty("comments")
            var comments: Int,
            @JsonProperty("comments_url")
            var commentsUrl: String,
            @JsonProperty("created_at")
            var createdAt: String,
            @JsonProperty("events_url")
            var eventsUrl: String,
            @JsonProperty("html_url")
            var htmlUrl: String,
            @JsonProperty("id")
            var id: Int,
            @JsonProperty("labels")
            var labels: List<IssueLabel>,
            @JsonProperty("labels_url")
            var labelsUrl: String,
            @JsonProperty("locked")
            var locked: Boolean,
            @JsonProperty("milestone")
            var milestone: Any?,
            @JsonProperty("node_id")
            var nodeId: String,
            @JsonProperty("number")
            var number: Int,
            @JsonProperty("pull_request")
            var pullRequest: PullRequest,
            @JsonProperty("repository_url")
            var repositoryUrl: String,
            @JsonProperty("state")
            var state: String,
            @JsonProperty("title")
            var title: String,
            @JsonProperty("updated_at")
            var updatedAt: String,
            @JsonProperty("url")
            var url: String,
            @JsonProperty("user")
            var user: User
    ) {
        data class PullRequest(
                @JsonProperty("diff_url")
                var diffUrl: String,
                @JsonProperty("html_url")
                var htmlUrl: String,
                @JsonProperty("patch_url")
                var patchUrl: String,
                @JsonProperty("url")
                var url: String
        )
    }
}

data class IssueLabel(
        @JsonProperty("color")
        var color: String,
        @JsonProperty("default")
        var default: Boolean,
        @JsonProperty("description")
        var description: Any?,
        @JsonProperty("id")
        var id: Int,
        @JsonProperty("name")
        var name: String,
        @JsonProperty("node_id")
        var nodeId: String,
        @JsonProperty("url")
        var url: String
)

data class IssueComment(
        @JsonProperty("author_association")
        var authorAssociation: AuthorAssociation,
        @JsonProperty("body")
        var body: String,
        @JsonProperty("created_at")
        var createdAt: String,
        @JsonProperty("html_url")
        var htmlUrl: String,
        @JsonProperty("id")
        var id: Int,
        @JsonProperty("issue_url")
        var issueUrl: String,
        @JsonProperty("node_id")
        var nodeId: String,
        @JsonProperty("updated_at")
        var updatedAt: String,
        @JsonProperty("url")
        var url: String,
        @JsonProperty("user")
        var user: User
)