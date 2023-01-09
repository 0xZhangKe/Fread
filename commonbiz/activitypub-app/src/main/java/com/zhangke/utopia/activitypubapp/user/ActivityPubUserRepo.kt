package com.zhangke.utopia.activitypubapp.user

import com.zhangke.activitypub.entry.ActivityPubToken
import kotlinx.coroutines.runBlocking

object ActivityPubUserRepo {

    private val userDao: ActivityPubUserDao get() = ActivityPubUserDatabase.instance.getActivityPubUserDao()

    private var currentUser: ActivityPubUser? = null

    /**
     * Get all the logged users.
     */
    suspend fun getAllUsers(): List<ActivityPubUser> {
        return userDao.queryAll().map { it.toUser() }
    }

    fun getCurrentUser(): ActivityPubUser? {
        var user = currentUser
        if (user == null) {
            user = runBlocking {
                userDao.queryAll()
                    .firstOrNull { it.selected }
                    ?.toUser()
            }
        }
        return user
    }

    suspend fun setCurrentUser(user: ActivityPubUser) {
        if (!user.selected) throw IllegalArgumentException("Current user is not select!")
        this.currentUser = user
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

    suspend fun setNormalUser(user: ActivityPubUser) {
        if (user.selected) throw IllegalArgumentException("Normal user is selected!")
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