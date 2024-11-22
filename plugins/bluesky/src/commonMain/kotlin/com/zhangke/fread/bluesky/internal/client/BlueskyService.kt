package com.zhangke.fread.bluesky.internal.client

import io.ktor.client.HttpClient
import sh.christian.ozone.BlueskyApi
import sh.christian.ozone.XrpcBlueskyApi

class BlueskyService(
    httpClient: HttpClient,
) : BlueskyApi by XrpcBlueskyApi(httpClient)