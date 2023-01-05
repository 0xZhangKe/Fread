package com.zhangke.utopia.activitypubapp.user

import com.zhangke.activitypub.entry.ActivityPubToken

object ActivityPubUserRepo {

    private val userDao: ActivityPubUserDao get() = ActivityPubUserDatabase.instance.getActivityPubUserDao()

    /**
     * Get all the logged users.
     */
    suspend fun getAllUsers(): List<ActivityPubUser> {
        return userDao.queryAll().map { it.toUser() }
    }

    suspend fun getCurrentUser(): ActivityPubUser? {
        return userDao.queryAll()
            .firstOrNull { it.selected }
            ?.toUser()
    }

    suspend fun updateCurrentUser(user: ActivityPubUser) {
        userDao.queryAll()
            .let { list ->
                if (list.firstOrNull { it.id == user.id } == null) {
                    val newList = list.toMutableList()
                    newList.add(user.toEntry())
                    newList
                } else {
                    list
                }
            }
            .map { it.withNewSelected(user.id == it.id) }
            .let { userDao.insert(it) }
    }

    suspend fun insertUser(user: ActivityPubUser) {
        userDao.insert(user.toEntry())
    }

    private fun ActivityPubUserEntry.toUser(): ActivityPubUser {
        return ActivityPubUser(
            id = id,
            domain = domain,
            name = name,
            avatar = avatar,
            description = description,
            homePage = homePage,
            selected = selected,
            token = ActivityPubToken(
                accessToken = accessToken,
                tokenType = tokenType,
                scope = scope,
                createdAt = createdAt
            )
        )
    }

    private fun ActivityPubUserEntry.withNewSelected(selected: Boolean): ActivityPubUserEntry {
        return ActivityPubUserEntry(
            id = id,
            domain = domain,
            name = name,
            avatar = avatar,
            description = description,
            homePage = homePage,
            selected = selected,
            accessToken = accessToken,
            tokenType = tokenType,
            scope = scope,
            createdAt = createdAt
        )
    }

    private fun ActivityPubUser.toEntry(): ActivityPubUserEntry {
        return ActivityPubUserEntry(
            id = id,
            domain = domain,
            name = name,
            avatar = avatar,
            description = description,
            homePage = homePage,
            selected = selected,
            accessToken = token.accessToken,
            tokenType = token.tokenType,
            scope = token.scope,
            createdAt = token.createdAt
        )
    }
}