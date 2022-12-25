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
class AccountRepoTest {

    private val retrofit = newTestRetrofit(CMIM)
    private val activityPub =
        ActivityPubClient(mastodonUtopiaApplication, retrofit, Gson(), onAuthorizeFailed = {})

    @Test
    fun testLookup() {
        runBlocking {
            activityPub.accountRepo.lookup("@Leeing@mastodon.social")
                .onSuccess {
                    println(it)
                }
                .onFailure {
                    println(it.stackTraceToString())
                }
        }
    }

    @Test
    fun testGetAccount() {
        runBlocking {
            activityPub.accountRepo.getAccount("107698947344203903")
                .onSuccess {
                    println(it)
                }
                .onFailure {
                    println(it.stackTraceToString())
                }
        }
    }

    @Test
    fun testGetStatuses() {
        runBlocking {
            activityPub.accountRepo.getStatuses("107698947344203903")
                .onSuccess {
                    println(it)
                }
                .onFailure {
                    println(it.stackTraceToString())
                }
        }
    }
}