package com.zhangke.fread.status.model

import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.fread.status.uri.FormalUri
import kotlin.test.Test
import kotlin.test.assertEquals

class IdentityRoleTest {
    @Test
    fun test() {
        val role = IdentityRole(
            FormalUri.from("freadapp://activitypub.com/user?finger=@seiko_des@mastodon.social"),
            FormalBaseUrl.build("https", "mastodon.social"),
        )
        assertEquals(
            "%7B%22accountUri%22%3A%7B%22host%22%3A%22activitypub.com%22%2C%22rawPath%22%3A%22%2Fuser%22%2C%22queries%22%3A%7B%22finger%22%3A%22%40seiko_des%40mastodon.social%22%7D%7D%2C%22baseUrl%22%3A%7B%22scheme%22%3A%22https%22%2C%22host%22%3A%22mastodon.social%22%7D%7D",
            role.encodeToUrlString(),
        )
        assertEquals(
            role,
            IdentityRole.decodeFromString(role.encodeToUrlString()),
        )
    }
}