package com.zhangke.activitypub.utils

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

internal fun newTestRetrofit(baseUrl: String): Retrofit =
    Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create(testGlobalGson))
        .client(TestOkHttpClient.client)
        .build()