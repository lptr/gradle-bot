package org.gradle.bot.model

import com.fasterxml.jackson.annotation.JsonProperty


/*
Header:

Request URL: http://webhook.yinghekongjian.com:5000/events
Request method: POST
content-type: application/json
Expect:
User-Agent: GitHub-Hookshot/6c4e595
X-GitHub-Delivery: 5521c100-6d78-11ea-8f39-8c184a370b63
X-GitHub-Event: pull_request
Payload
{
  "action": "closed",
  "number": 99,
  "pull_request": {
    "url": "https://api.github.com/repos/hcsp/save-pull-requests-to-csv/pulls/99",
    "id": 392009701,
    "node_id": "MDExOlB1bGxSZXF1ZXN0MzkyMDA5NzAx",
    "html_url": "https://github.com/hcsp/save-pull-requests-to-csv/pull/99",
    "diff_url": "https://github.com/hcsp/save-pull-requests-to-csv/pull/99.diff",
    "patch_url": "https://github.com/hcsp/save-pull-requests-to-csv/pull/99.patch",
    "issue_url": "https://api.github.com/repos/hcsp/save-pull-requests-to-csv/issues/99",
    "number": 99,
    "state": "closed",
    "locked": false,
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
    "body": "<!--- 你要打开的这个Pull request(PR)的类型是？默认是题目解答，如果你正在修复当前的仓库的缺陷，请选择对应的类型 -->\r\n\r\n- [x] 这个PR解答了当前仓库中的题目（机器人会自动判题并合并当前PR）\r\n- [ ] 这个PR修复了当前仓库中的一些代码缺陷（机器人不会判题，而是由管理员来处理当前PR）\r\n\r\n",
    "created_at": "2020-03-22T13:56:37Z",
    "updated_at": "2020-03-24T02:36:58Z",
    "closed_at": "2020-03-24T02:36:58Z",
    "merged_at": "2020-03-24T02:36:58Z",
    "merge_commit_sha": "f2009746fcf196b8432491eee3b8f67245b864f2",
    "assignee": null,
    "assignees": [

    ],
    "requested_reviewers": [

    ],
    "requested_teams": [

    ],
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
    "milestone": null,
    "draft": false,
    "commits_url": "https://api.github.com/repos/hcsp/save-pull-requests-to-csv/pulls/99/commits",
    "review_comments_url": "https://api.github.com/repos/hcsp/save-pull-requests-to-csv/pulls/99/comments",
    "review_comment_url": "https://api.github.com/repos/hcsp/save-pull-requests-to-csv/pulls/comments{/number}",
    "comments_url": "https://api.github.com/repos/hcsp/save-pull-requests-to-csv/issues/99/comments",
    "statuses_url": "https://api.github.com/repos/hcsp/save-pull-requests-to-csv/statuses/ae1943cfeba4533d05335ff5d417297411d722cc",
    "head": {
      "label": "hcsp:LIU",
      "ref": "LIU",
      "sha": "ae1943cfeba4533d05335ff5d417297411d722cc",
      "user": {
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
      "repo": {
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
        "updated_at": "2020-03-23T04:51:21Z",
        "pushed_at": "2020-03-24T02:36:58Z",
        "git_url": "git://github.com/hcsp/save-pull-requests-to-csv.git",
        "ssh_url": "git@github.com:hcsp/save-pull-requests-to-csv.git",
        "clone_url": "https://github.com/hcsp/save-pull-requests-to-csv.git",
        "svn_url": "https://github.com/hcsp/save-pull-requests-to-csv",
        "homepage": null,
        "size": 217,
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
      }
    },
    "base": {
      "label": "hcsp:master",
      "ref": "master",
      "sha": "b008b012e73bc2941e8e2ef3bbd1ab5fdf11e97d",
      "user": {
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
      "repo": {
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
        "updated_at": "2020-03-23T04:51:21Z",
        "pushed_at": "2020-03-24T02:36:58Z",
        "git_url": "git://github.com/hcsp/save-pull-requests-to-csv.git",
        "ssh_url": "git@github.com:hcsp/save-pull-requests-to-csv.git",
        "clone_url": "https://github.com/hcsp/save-pull-requests-to-csv.git",
        "svn_url": "https://github.com/hcsp/save-pull-requests-to-csv",
        "homepage": null,
        "size": 217,
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
      }
    },
    "_links": {
      "self": {
        "href": "https://api.github.com/repos/hcsp/save-pull-requests-to-csv/pulls/99"
      },
      "html": {
        "href": "https://github.com/hcsp/save-pull-requests-to-csv/pull/99"
      },
      "issue": {
        "href": "https://api.github.com/repos/hcsp/save-pull-requests-to-csv/issues/99"
      },
      "comments": {
        "href": "https://api.github.com/repos/hcsp/save-pull-requests-to-csv/issues/99/comments"
      },
      "review_comments": {
        "href": "https://api.github.com/repos/hcsp/save-pull-requests-to-csv/pulls/99/comments"
      },
      "review_comment": {
        "href": "https://api.github.com/repos/hcsp/save-pull-requests-to-csv/pulls/comments{/number}"
      },
      "commits": {
        "href": "https://api.github.com/repos/hcsp/save-pull-requests-to-csv/pulls/99/commits"
      },
      "statuses": {
        "href": "https://api.github.com/repos/hcsp/save-pull-requests-to-csv/statuses/ae1943cfeba4533d05335ff5d417297411d722cc"
      }
    },
    "author_association": "NONE",
    "merged": true,
    "mergeable": null,
    "rebaseable": null,
    "mergeable_state": "unknown",
    "merged_by": {
      "login": "hcsp-bot",
      "id": 49554844,
      "node_id": "MDQ6VXNlcjQ5NTU0ODQ0",
      "avatar_url": "https://avatars2.githubusercontent.com/u/49554844?v=4",
      "gravatar_id": "",
      "url": "https://api.github.com/users/hcsp-bot",
      "html_url": "https://github.com/hcsp-bot",
      "followers_url": "https://api.github.com/users/hcsp-bot/followers",
      "following_url": "https://api.github.com/users/hcsp-bot/following{/other_user}",
      "gists_url": "https://api.github.com/users/hcsp-bot/gists{/gist_id}",
      "starred_url": "https://api.github.com/users/hcsp-bot/starred{/owner}{/repo}",
      "subscriptions_url": "https://api.github.com/users/hcsp-bot/subscriptions",
      "organizations_url": "https://api.github.com/users/hcsp-bot/orgs",
      "repos_url": "https://api.github.com/users/hcsp-bot/repos",
      "events_url": "https://api.github.com/users/hcsp-bot/events{/privacy}",
      "received_events_url": "https://api.github.com/users/hcsp-bot/received_events",
      "type": "User",
      "site_admin": false
    },
    "comments": 5,
    "review_comments": 0,
    "maintainer_can_modify": false,
    "commits": 4,
    "additions": 70,
    "deletions": 26,
    "changed_files": 2
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
    "updated_at": "2020-03-23T04:51:21Z",
    "pushed_at": "2020-03-24T02:36:58Z",
    "git_url": "git://github.com/hcsp/save-pull-requests-to-csv.git",
    "ssh_url": "git@github.com:hcsp/save-pull-requests-to-csv.git",
    "clone_url": "https://github.com/hcsp/save-pull-requests-to-csv.git",
    "svn_url": "https://github.com/hcsp/save-pull-requests-to-csv",
    "homepage": null,
    "size": 217,
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
    "login": "hcsp-bot",
    "id": 49554844,
    "node_id": "MDQ6VXNlcjQ5NTU0ODQ0",
    "avatar_url": "https://avatars2.githubusercontent.com/u/49554844?v=4",
    "gravatar_id": "",
    "url": "https://api.github.com/users/hcsp-bot",
    "html_url": "https://github.com/hcsp-bot",
    "followers_url": "https://api.github.com/users/hcsp-bot/followers",
    "following_url": "https://api.github.com/users/hcsp-bot/following{/other_user}",
    "gists_url": "https://api.github.com/users/hcsp-bot/gists{/gist_id}",
    "starred_url": "https://api.github.com/users/hcsp-bot/starred{/owner}{/repo}",
    "subscriptions_url": "https://api.github.com/users/hcsp-bot/subscriptions",
    "organizations_url": "https://api.github.com/users/hcsp-bot/orgs",
    "repos_url": "https://api.github.com/users/hcsp-bot/repos",
    "events_url": "https://api.github.com/users/hcsp-bot/events{/privacy}",
    "received_events_url": "https://api.github.com/users/hcsp-bot/received_events",
    "type": "User",
    "site_admin": false
  }
}
 */

data class PullRequestEvent(
        @JsonProperty("action")
        var action: String,
        @JsonProperty("number")
        var number: Int,
        @JsonProperty("organization")
        override var organization: Organization,
        @JsonProperty("pull_request")
        var pullRequest: PullRequest,
        @JsonProperty("repository")
        override var repository: Repository,
        @JsonProperty("sender")
        override var sender: User
) : GitHubEvent {
    data class PullRequest(
            @JsonProperty("additions")
            var additions: Int,
            @JsonProperty("assignee")
            var assignee: Any,
            @JsonProperty("assignees")
            var assignees: List<Any>,
            @JsonProperty("author_association")
            var authorAssociation: String,
            @JsonProperty("base")
            var base: PullRequestBranch,
            @JsonProperty("body")
            var body: String,
            @JsonProperty("changed_files")
            var changedFiles: Int,
            @JsonProperty("closed_at")
            var closedAt: String,
            @JsonProperty("comments")
            var comments: Int,
            @JsonProperty("comments_url")
            var commentsUrl: String,
            @JsonProperty("commits")
            var commits: Int,
            @JsonProperty("commits_url")
            var commitsUrl: String,
            @JsonProperty("created_at")
            var createdAt: String,
            @JsonProperty("deletions")
            var deletions: Int,
            @JsonProperty("diff_url")
            var diffUrl: String,
            @JsonProperty("draft")
            var draft: Boolean,
            @JsonProperty("head")
            var head: PullRequestBranch,
            @JsonProperty("html_url")
            var htmlUrl: String,
            @JsonProperty("id")
            var id: Int,
            @JsonProperty("issue_url")
            var issueUrl: String,
            @JsonProperty("labels")
            var labels: List<IssueLabel>,
            @JsonProperty("_links")
            var links: Links,
            @JsonProperty("locked")
            var locked: Boolean,
            @JsonProperty("maintainer_can_modify")
            var maintainerCanModify: Boolean,
            @JsonProperty("merge_commit_sha")
            var mergeCommitSha: String,
            @JsonProperty("mergeable")
            var mergeable: Any,
            @JsonProperty("mergeable_state")
            var mergeableState: String,
            @JsonProperty("merged")
            var merged: Boolean,
            @JsonProperty("merged_at")
            var mergedAt: String,
            @JsonProperty("merged_by")
            var mergedBy: User,
            @JsonProperty("milestone")
            var milestone: Any,
            @JsonProperty("node_id")
            var nodeId: String,
            @JsonProperty("number")
            var number: Int,
            @JsonProperty("patch_url")
            var patchUrl: String,
            @JsonProperty("rebaseable")
            var rebaseable: Any,
            @JsonProperty("requested_reviewers")
            var requestedReviewers: List<Any>,
            @JsonProperty("requested_teams")
            var requestedTeams: List<Any>,
            @JsonProperty("review_comment_url")
            var reviewCommentUrl: String,
            @JsonProperty("review_comments")
            var reviewComments: Int,
            @JsonProperty("review_comments_url")
            var reviewCommentsUrl: String,
            @JsonProperty("state")
            var state: String,
            @JsonProperty("statuses_url")
            var statusesUrl: String,
            @JsonProperty("title")
            var title: String,
            @JsonProperty("updated_at")
            var updatedAt: String,
            @JsonProperty("url")
            var url: String,
            @JsonProperty("user")
            var user: User
    ) {
        data class Links(
                @JsonProperty("comments")
                var comments: PullRequestLink,
                @JsonProperty("commits")
                var commits: PullRequestLink,
                @JsonProperty("html")
                var html: PullRequestLink,
                @JsonProperty("issue")
                var issue: PullRequestLink,
                @JsonProperty("review_comment")
                var reviewComment: PullRequestLink,
                @JsonProperty("review_comments")
                var reviewComments: PullRequestLink,
                @JsonProperty("self")
                var self: PullRequestLink,
                @JsonProperty("statuses")
                var statuses: PullRequestLink
        )
    }
}

data class PullRequestLink(
        @JsonProperty("href")
        var href: String
)

data class PullRequestBranch(
        @JsonProperty("label")
        var label: String,
        @JsonProperty("ref")
        var ref: String,
        @JsonProperty("repo")
        var repo: PullRequestEvent,
        @JsonProperty("sha")
        var sha: String,
        @JsonProperty("user")
        var user: User
)