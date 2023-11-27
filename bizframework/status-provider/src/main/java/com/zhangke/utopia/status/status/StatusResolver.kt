package com.zhangke.utopia.status.status

import android.net.Uri
import com.zhangke.framework.collections.mapFirstOrNull
import com.zhangke.framework.feeds.fetcher.FeedsFetcher
import com.zhangke.framework.feeds.fetcher.FeedsGenerator
import com.zhangke.framework.feeds.fetcher.StatusDataSource
import com.zhangke.utopia.status.account.LoggedAccount
import com.zhangke.utopia.status.uri.StatusProviderUri

class StatusResolver(
    private val resolverList: List<IStatusResolver>,
) {

    suspend fun uploadMediaAttachment(
        account: LoggedAccount,
        fileUri: Uri,
        description: String? = null,
        onProgress: (Float) -> Unit,
    ): Result<UploadAttachmentMediaResult> {
        resolverList.forEach { resolver ->
            val result = resolver.uploadMediaAttachment(
                account = account,
                fileUri = fileUri,
                description = description,
                onProgress = onProgress
            )
            if (result != null) {
                return result
            }
        }
        return Result.failure(IllegalArgumentException("Invalid account!"))
    }

    fun getStatusDataSourceByUri(
        uris: List<String>
    ): List<StatusDataSource<*, Status>> {
        return uris.map { StatusProviderUri.from(it)!! }
            .mapNotNull { uri -> resolverList.mapFirstOrNull { it.getStatusDataSourceByUri(uri) } }
    }

    fun getStatusFeedsByUris(
        uris: List<String>,
        pageSize: Int,
    ): FeedsFetcher<Status> {
        return FeedsFetcher(getStatusDataSourceByUri(uris), pageSize, FeedsGenerator())
    }
}

interface IStatusResolver {

    suspend fun uploadMediaAttachment(
        account: LoggedAccount,
        fileUri: Uri,
        description: String?,
        onProgress: (Float) -> Unit,
    ): Result<UploadAttachmentMediaResult>?

    fun getStatusDataSourceByUri(uri: StatusProviderUri): StatusDataSource<*, Status>?

    fun postStatus()
}
