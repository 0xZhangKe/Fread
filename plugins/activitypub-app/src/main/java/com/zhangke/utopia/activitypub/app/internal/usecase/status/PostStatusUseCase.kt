package com.zhangke.utopia.activitypub.app.internal.usecase.status

import com.zhangke.utopia.activitypub.app.internal.adapter.PostStatusAttachmentAdapter
import com.zhangke.utopia.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.utopia.activitypub.app.internal.model.ActivityPubLoggedAccount
import com.zhangke.utopia.activitypub.app.internal.model.PostStatusVisibility
import com.zhangke.utopia.activitypub.app.internal.screen.status.post.PostStatusAttachment
import com.zhangke.utopia.activitypub.app.internal.screen.status.post.UploadMediaJob
import com.zhangke.utopia.status.model.IdentityRole
import java.util.Locale
import javax.inject.Inject

class PostStatusUseCase @Inject constructor(
    private val clientManager: ActivityPubClientManager,
    private val attachmentAdapter: PostStatusAttachmentAdapter,
) {

    suspend operator fun invoke(
        account: ActivityPubLoggedAccount,
        content: String?,
        attachment: PostStatusAttachment?,
        sensitive: Boolean? = null,
        spoilerText: String? = null,
        replyToId: String? = null,
        visibility: PostStatusVisibility? = null,
        language: Locale? = null,
    ): Result<Unit> {
        val role = IdentityRole(account.uri, null)
        val statusRepo = clientManager.getClient(role).statusRepo
        val mediaIds = when (attachment) {
            is PostStatusAttachment.VideoAttachment -> {
                val videoMediaId = (attachment.video.uploadJob.uploadState.value as? UploadMediaJob.UploadState.Success)?.id
                if (videoMediaId.isNullOrEmpty()) null else listOf(videoMediaId)
            }

            is PostStatusAttachment.ImageAttachment -> {
                attachment.imageList
                    .map { it.uploadJob.uploadState.value }
                    .mapNotNull { it as? UploadMediaJob.UploadState.Success }
                    .map { it.id }
            }

            else -> null
        }

        statusRepo.postStatus(
            status = content,
            mediaIds = mediaIds,
            poll = attachment?.asPollAttachmentOrNull?.let(attachmentAdapter::toPollRequest),
            sensitive = sensitive,
            replyToId = replyToId,
            spoilerText = spoilerText,
            visibility = visibility?.toEntity(),
            language = language?.isO3Language,
        )
        return Result.success(Unit)
    }
}
