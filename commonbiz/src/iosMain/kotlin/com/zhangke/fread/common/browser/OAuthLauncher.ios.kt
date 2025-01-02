package com.zhangke.fread.common.browser

import com.zhangke.fread.common.di.ApplicationScope
import kotlinx.coroutines.suspendCancellableCoroutine
import me.tatarka.inject.annotations.Inject
import platform.AuthenticationServices.ASWebAuthenticationPresentationContextProvidingProtocol
import platform.AuthenticationServices.ASWebAuthenticationSession
import platform.Foundation.NSError
import platform.Foundation.NSURL
import platform.Foundation.NSURLComponents
import platform.Foundation.NSURLQueryItem
import platform.UIKit.UIApplication
import platform.UIKit.UIWindow
import platform.darwin.NSObject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@ApplicationScope
actual class OAuthHandler @Inject constructor(
    private val application: UIApplication,
) {
    @Suppress("UNCHECKED_CAST")
    actual suspend fun startOAuth(url: String): String = suspendCancellableCoroutine { continuation ->
        val webAuthSession = ASWebAuthenticationSession(
            uRL = NSURL(string = url),
            callbackURLScheme = "freadapp"
        ) { callbackURL: NSURL?, error: NSError? ->
            when {
                error != null -> {
                    when (error.code) {
                        1L -> {
                            continuation.cancel()
                        }
                        else -> {
                            continuation.resumeWithException(RuntimeException(error.localizedDescription))
                        }
                    }
                }
                callbackURL != null -> {
                    val components = NSURLComponents(uRL = callbackURL, resolvingAgainstBaseURL = false)
                    val queryItems = components.queryItems as? List<NSURLQueryItem>
                    val code = queryItems?.firstOrNull {
                        it.name == "code"
                    }?.value

                    if (code != null) {
                        continuation.resume(code)
                    } else {
                        continuation.resumeWithException(RuntimeException("No code received"))
                    }
                }
                else -> {
                    continuation.cancel()
                }
            }
        }

        val contextProvider =
            object : NSObject(), ASWebAuthenticationPresentationContextProvidingProtocol {
                override fun presentationAnchorForWebAuthenticationSession(
                    session: ASWebAuthenticationSession
                ): UIWindow {
                    return application.keyWindow!!
                }

                override fun description(): String {
                    return "ASWebAuthenticationPresentationContextProvider"
                }

                override fun hash(): ULong {
                    return hashCode().toULong()
                }

                override fun isEqual(`object`: Any?): Boolean {
                    return this === `object`
                }
            }

        webAuthSession.presentationContextProvider = contextProvider
        webAuthSession.prefersEphemeralWebBrowserSession = false
        webAuthSession.start()
    }
}