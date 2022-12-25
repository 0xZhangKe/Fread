package com.zhangke.activitypub.api

import com.google.gson.Gson
import com.zhangke.activitypub.ActivityPubClient
import com.zhangke.activitypub.utils.CMIM
import com.zhangke.activitypub.utils.newTestRetrofit
import kotlinx.coroutines.runBlocking
import org.junit.Test

/**
 * Created by ZhangKe on 2022/12/2.
 */

class RegisterApiTest {

    private val retrofit = newTestRetrofit(CMIM)
    private val activityPub = ActivityPubClient(mastodonUtopiaApplication, retrofit, Gson(), onAuthorizeFailed = {})

    @Test
    fun testRegister() {
        runBlocking {
            try {
                activityPub.oauthRepo.registerApplication(
                    clientName = "utopia",
                    redirectUris = listOf("utopia://oauth.utopia"),
                    scopes = RegisterRepo.AppScopes.ALL,
                    website = "https://0xzhangke.github.io/"
                ).onFailure {
                    println("onError:$it")
                    assert(false)
                }.onSuccess {
                    println("onSuccess:$it")
                    assert(true)
                }
                assert(true)
            } catch (e: Throwable) {
                e.printStackTrace()
                assert(false)
            }
        }
    }

    @Test
    fun testRegisterIllegalRequest() {
        runBlocking {
            try {
                activityPub.oauthRepo.registerApplication(
                    clientName = "utopia",
                    redirectUris = emptyList(),
                    scopes = RegisterRepo.AppScopes.ALL,
                    website = "https://0xzhangke.github.io/"
                ).onFailure {
                    println("onError:$it")
                    assert(false)
                }.onSuccess {
                    println("onSuccess:$it")
                    assert(true)
                }
                assert(true)
            } catch (e: Throwable) {
                e.printStackTrace()
                assert(false)
            }
        }
    }

    @Test
    fun testRegisterIOError() {
        runBlocking {
            try {
                activityPub.oauthRepo.registerApplication(
                    clientName = "utopia",
                    redirectUris = listOf("https://0xzhangke.github.io/"),
                    scopes = RegisterRepo.AppScopes.ALL,
                    website = "https://0xzhangke.github.io/"
                ).onFailure {
                    println("onError:$it")
                    assert(false)
                }.onSuccess {
                    println("onSuccess:$it")
                    assert(true)
                }
                assert(true)
            } catch (e: Throwable) {
                e.printStackTrace()
                assert(false)
            }
        }
    }
}
