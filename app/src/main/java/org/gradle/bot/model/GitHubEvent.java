package org.gradle.bot.model;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;

public interface GitHubEvent {
    BiMap<String, Class<? extends GitHubEvent>> EVENT_TYPES = ImmutableBiMap.<String, Class<? extends GitHubEvent>>builder()
            .put("status", CommitStatusGitHubEvent.class)
            .put("pull_request", PullRequestGitHubEvent.class)
            .put("issue_comment", IssueCommentGitHubEvent.class)
            .build();

    default String getEventType() {
        return EVENT_TYPES.inverse().get(getClass());
    }

    static Class<? extends GitHubEvent> getEventClassByType(String eventType) {
        return EVENT_TYPES.get(eventType);
    }

}
