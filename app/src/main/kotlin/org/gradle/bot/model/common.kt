package org.gradle.bot.model

import org.jetbrains.teamcity.rest.BuildStatus

// https://developer.github.com/v4/enum/commentauthorassociation/
enum class AuthorAssociation(val admin: Boolean) {
    COLLABORATOR(true),
    CONTRIBUTOR(false),
    FIRST_TIMER(false),
    FIRST_TIME_CONTRIBUTOR(false),
    MEMBER(true),
    NONE(false),
    OWNER(true);

    companion object {
        fun isAdmin(rawValue: String) = valueOf(rawValue).admin
    }
}

enum class CommitStatusState {
    PENDING,
    ERROR,
    FAILURE,
    SUCCESS;

    companion object {
        fun fromTeamCityBuildStatus(buildStatus: BuildStatus): CommitStatusState =
            when (buildStatus) {
                BuildStatus.SUCCESS -> SUCCESS
                BuildStatus.ERROR -> ERROR
                BuildStatus.FAILURE -> FAILURE
                BuildStatus.UNKNOWN -> PENDING
            }

        fun of(value: String) = valueOf(value.toUpperCase())
    }
}
