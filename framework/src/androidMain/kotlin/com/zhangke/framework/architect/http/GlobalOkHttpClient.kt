package com.zhangke.framework.architect.http

import android.annotation.SuppressLint
import android.util.Log
import com.zhangke.framework.utils.ifDebugging
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.security.KeyStore
import java.util.*
import java.util.concurrent.TimeUnit
import javax.net.ssl.*

object GlobalOkHttpClient {

    private const val TIMEOUT = 15L

    val client: OkHttpClient by lazy { createBuilder().build() }

    private val thirdPartInterceptors = mutableListOf<Interceptor>()

    fun addThirdPartInterceptor(interceptor: Interceptor) {
        thirdPartInterceptors += interceptor
    }

    private fun createBuilder(): OkHttpClient.Builder {
        val ssl = buildSSLFactory()
        val builder = OkHttpClient().newBuilder()
            .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
            .sslSocketFactory(ssl.first, ssl.second)
            .hostnameVerifier { _, _ -> true }
        ifDebugging {
            builder.addInterceptor(
                HttpLoggingInterceptor(HttpLoggingInterceptor.Logger.DEFAULT)
                    .setLevel(HttpLoggingInterceptor.Level.BODY)
            )
        }
        thirdPartInterceptors.forEach {
            Log.d("GlobalOkHttpClient", "add $it")
            builder.addInterceptor(it)
        }
        return builder
    }

    @SuppressLint("TrustAllX509TrustManager", "CustomX509TrustManager")
    private fun buildSSLFactory(): Pair<SSLSocketFactory, X509TrustManager> {
        val trustManagerFactory =
            TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        trustManagerFactory.init(null as KeyStore?)
        val trustManagers = trustManagerFactory.trustManagers
        check(!(trustManagers.size != 1 || trustManagers[0] !is X509TrustManager)) {
            "Unexpected default trust managers:" + Arrays.toString(trustManagers)
        }
        val trustManager = trustManagers[0] as X509TrustManager
        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, arrayOf<TrustManager>(trustManager), null)
        sslContext.defaultSSLParameters.protocols = arrayOf("SSLv3")
        val sslSocketFactory = sslContext.socketFactory
        return Pair(sslSocketFactory, trustManager)
    }
}