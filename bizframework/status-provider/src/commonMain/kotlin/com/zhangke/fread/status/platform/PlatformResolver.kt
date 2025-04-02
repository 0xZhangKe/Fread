package com.zhangke.fread.status.platform

import com.zhangke.framework.collections.mapFirst
import com.zhangke.framework.collections.mapFirstOrNull
import com.zhangke.fread.status.uri.FormalUri

class PlatformResolver(
    private val resolverList: List<IPlatformResolver>,
) {

    suspend fun resolve(blogSnapshot: PlatformSnapshot): Result<BlogPlatform> {
        return resolverList.mapFirst { it.resolve(blogSnapshot) }
    }

    suspend fun getSuggestedPlatformList(): List<PlatformSnapshot> {
        return resolverList.map { it.getSuggestedPlatformSnapshotList() }.flatten()
    }
}

interface IPlatformResolver {

    suspend fun resolve(blogSnapshot: PlatformSnapshot): Result<BlogPlatform>?

    suspend fun getSuggestedPlatformSnapshotList(): List<PlatformSnapshot>
}
