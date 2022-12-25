package com.zhangke.activitypub.api

import com.google.gson.Gson
import com.zhangke.activitypub.ActivityPubClient
import com.zhangke.activitypub.utils.CMIM
import com.zhangke.activitypub.utils.newTestRetrofit
import kotlinx.coroutines.runBlocking
import org.junit.Test

/**
 * Created by ZhangKe on 2022/12/13.
 */
class TimelinesRepoTest {

    private val retrofit = newTestRetrofit(CMIM)
    private val activityPub =
        ActivityPubClient(mastodonUtopiaApplication, retrofit, Gson(), onAuthorizeFailed = {})

    @Test
    fun testLocalTimelines() {
        runBlocking {
            activityPub.timelinesRepo.localTimelines()
                .onFailure {
                    println(it.stackTraceToString())
                }
                .onSuccess {
                    println(it)
                }
        }
    }

    @Test
    fun testPublicTimelines() {
        runBlocking {
            activityPub.timelinesRepo.localTimelines()
                .onFailure {
                    println(it.stackTraceToString())
                }
                .onSuccess {
                    println(it)
                }
        }
    }
}