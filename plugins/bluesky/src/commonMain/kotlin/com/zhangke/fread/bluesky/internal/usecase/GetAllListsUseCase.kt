package com.zhangke.fread.bluesky.internal.usecase

import app.bsky.graph.GetListsQueryParams
import app.bsky.graph.ListView
import com.zhangke.fread.bluesky.internal.client.BlueskyClientManager
import com.zhangke.fread.status.model.PlatformLocator
import sh.christian.ozone.api.AtIdentifier

class GetAllListsUseCase(
    private val clientManager: BlueskyClientManager,
) {

    suspend operator fun invoke(
        locator: PlatformLocator,
        id: AtIdentifier,
    ): Result<List<ListView>> {
        val client = clientManager.getClient(locator)
        var cursor: String? = null
        val lists = mutableListOf<ListView>()
        while (true) {
            val result = client.getListsCatching(GetListsQueryParams(actor = id, cursor = cursor))
            if (result.isFailure) {
                if (lists.isEmpty()) {
                    return Result.failure(result.exceptionOrNull()!!)
                } else {
                    break
                }
            } else {
                val response = result.getOrThrow()
                lists.addAll(response.lists)
                if (response.cursor.isNullOrBlank()) {
                    break
                }
                cursor = response.cursor
            }
        }
        return Result.success(lists)
    }
}
