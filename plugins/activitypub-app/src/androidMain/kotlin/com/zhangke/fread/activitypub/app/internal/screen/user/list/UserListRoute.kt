package com.zhangke.fread.activitypub.app.internal.screen.user.list

import com.zhangke.framework.network.SimpleUri
import com.zhangke.fread.activitypub.app.internal.route.ActivityPubRoutes
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.model.encodeToUrlString

object UserListRoute {

    const val ROUTE = "${ActivityPubRoutes.ROOT}/user/list"

    private const val PARAM_ROLE = "role"
    private const val PARAM_TYPE = "type"
    private const val PARAM_BLOG_ID = "blogId"

    fun buildBlogBoostedRoute(
        role: IdentityRole,
        blogId: String,
    ): String {
        return buildString {
            append(ROUTE)
            append("?$PARAM_TYPE=${UserListType.REBLOGS.name}")
            append("&$PARAM_ROLE=${role.encodeToUrlString()}")
            append("&$PARAM_BLOG_ID=$blogId")
        }
    }

    fun buildBlogFavouritedRoute(
        role: IdentityRole,
        blogId: String,
    ): String {
        return buildString {
            append(ROUTE)
            append("?$PARAM_TYPE=${UserListType.FAVOURITES.name}")
            append("&$PARAM_ROLE=${role.encodeToUrlString()}")
            append("&$PARAM_BLOG_ID=$blogId")
        }
    }

    fun parseRouteAsReblogOrFavourited(route: String): Triple<IdentityRole, UserListType, String>? {
        val queries = SimpleUri.parse(route)!!.queries
        val role = queries[PARAM_ROLE]?.let { IdentityRole.decodeFromString(it) }
        val type = queries[PARAM_TYPE]?.let { UserListType.valueOf(it) }
        val blogId = queries[PARAM_BLOG_ID]
        if (role == null || type == null || blogId == null) {
            return null
        }
        return Triple(role, type, blogId)
    }
}
