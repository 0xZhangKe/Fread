package com.zhangke.framework.architect.http

import com.zhangke.framework.architect.json.globalGson
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

fun newRetrofit(baseUrl: String): Retrofit =
    Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create(globalGson))
        .client(GlobalOkHttpClient.client)
        .build()