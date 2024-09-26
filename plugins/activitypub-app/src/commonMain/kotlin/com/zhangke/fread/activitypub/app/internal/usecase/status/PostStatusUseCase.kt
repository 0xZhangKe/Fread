package com.zhangke.fread.activitypub.app.internal.usecase.status

import com.zhangke.activitypub.entities.ActivityPubEditStatusEntity
import com.zhangke.activitypub.entities.ActivityPubStatusVisibilityEntity
import com.zhangke.framework.utils.Locale
import com.zhangke.framework.utils.isO3LanguageCode
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.fread.activitypub.app.internal.adapter.PostStatusAttachmentAdapter
import com.zhangke.fread.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.fread.activitypub.app.internal.model.ActivityPubLoggedAccount
import com.zhangke.fread.activitypub.app.internal.screen.status.post.PostStatusAttachment
import com.zhangke.fread.activitypub.app.internal.screen.status.post.PostStatusMediaAttachmentFile
import com.zhangke.fread.common.status.StatusUpdater
import com.zhangke.fread.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.model.StatusVisibility
import me.tatarka.inject.annotations.Inject

class PostStatusUseCase @Inject constructor(
    private val clientManager: ActivityPubClientManager,
    private val statusUpdater: StatusUpdater,
    private val statusEntityAdapter: ActivityPubStatusAdapter,
    private val buildStatusUiState: BuildStatusUiStateUseCase,
    private val attachmentAdapter: PostStatusAttachmentAdapter,
) {

    /**
     * @param originStatusId edit status screen need provide that.
     */
    suspend operator fun invoke(
        account: ActivityPubLoggedAccount,
        content: String?,
        attachment: PostStatusAttachment?,
        originStatusId: String? = null,
        sensitive: Boolean? = null,
        spoilerText: String? = null,
        replyToId: String? = null,
        visibility: StatusVisibility? = null,
        language: Locale? = null,
    ): Result<Unit> {
        val role = IdentityRole(account.uri, null)
        val statusRepo = clientManager.getClient(role).statusRepo
        val mediaIds = when (attachment) {
            is PostStatusAttachment.Video -> {
                val videoMediaId = attachment.video.fileId
                if (videoMediaId.isNullOrEmpty()) null else listOf(videoMediaId)
            }

            is PostStatusAttachment.Image -> {
                attachment.imageList.mapNotNull { it.fileId }
            }

            else -> null
        }

        return if (originStatusId.isNullOrEmpty()) {
            statusRepo.postStatus(
                status = content,
                mediaIds = mediaIds,
                poll = attachment?.asPollAttachmentOrNull?.let(attachmentAdapter::toPollRequest),
                sensitive = sensitive,
                spoilerText = if (sensitive == true) spoilerText else null,
                replyToId = replyToId,
                visibility = visibility?.toEntityVisibility(),
                language = language?.isO3LanguageCode,
            ).map {
                val status = statusEntityAdapter.toStatus(it, account.platform)
                statusUpdater.update(buildStatusUiState(role, status))
            }
        } else {
            val mediaAttributes = mutableListOf<ActivityPubEditStatusEntity.MediaAttributes>()
            val mediaFileList = when (attachment) {
                is PostStatusAttachment.Image -> {
                    attachment.imageList
                }

                is PostStatusAttachment.Video -> {
                    listOf(attachment.video)
                }

                else -> emptyList()
            }
            mediaFileList.mapNotNull { it as? PostStatusMediaAttachmentFile.RemoteFile }.map {
                mediaAttributes += ActivityPubEditStatusEntity.MediaAttributes(
                    id = it.id,
                    description = it.description,
                )
            }
            statusRepo.editStatus(
                id = originStatusId,
                status = content,
                mediaIds = mediaIds,
                mediaAttributes = mediaAttributes,
                poll = attachment?.asPollAttachmentOrNull?.let(attachmentAdapter::toPollRequest),
                sensitive = sensitive,
                spoilerText = if (sensitive == true) spoilerText else null,
                language = language?.isO3LanguageCode,
            ).map {
                val status = statusEntityAdapter.toStatus(it, account.platform)
                statusUpdater.update(buildStatusUiState(role, status))
            }
        }
    }

    private fun StatusVisibility.toEntityVisibility(): ActivityPubStatusVisibilityEntity {
        return when (this) {
            StatusVisibility.PUBLIC -> ActivityPubStatusVisibilityEntity.PUBLIC
            StatusVisibility.UNLISTED -> ActivityPubStatusVisibilityEntity.UNLISTED
            StatusVisibility.PRIVATE -> ActivityPubStatusVisibilityEntity.PRIVATE
            StatusVisibility.DIRECT -> ActivityPubStatusVisibilityEntity.DIRECT
        }
    }
}
