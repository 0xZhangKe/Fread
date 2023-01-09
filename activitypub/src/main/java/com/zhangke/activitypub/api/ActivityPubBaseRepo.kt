package com.zhangke.activitypub.api

import com.zhangke.activitypub.ActivityPubClient
import com.zhangke.activitypub.exception.ActivityPubHttpException

/**
 * Created by ZhangKe on 2022/12/14.
 */
abstract class ActivityPubBaseRepo(protected val client: ActivityPubClient) {

    protected fun <T> createApi(clazz: Class<T>): T {
        return client.retrofit.create(clazz)
    }

    protected fun <T> Result<T>.collectAuthorizeFailed(): Result<T> {
        return onFailure {
            if (it is ActivityPubHttpException.UnauthorizedException) {
                client.onAuthorizeFailed(client.buildOAuthUrl(), client)
            }
        }
    }

    protected fun getAuthorizationHeader(): String {
        val accessToken = client.tokenProvider()?.accessToken
        return if (accessToken.isNullOrEmpty()) "" else buildAuthorizationHeader(accessToken)
    }

    protected fun buildAuthorizationHeader(accessToken: String): String {
        return "Bearer $accessToken"
    }
}