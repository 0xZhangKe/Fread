package com.zhangke.fread.activitypub.app.internal.screen.status.post.usecase

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
import com.zhangke.fread.activitypub.app.internal.screen.status.post.PostStatusUiState
import com.zhangke.fread.activitypub.app.internal.usecase.media.UploadMediaAttachmentUseCase
import com.zhangke.fread.common.status.StatusUpdater
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.model.StatusVisibility
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.supervisorScope
import me.tatarka.inject.annotations.Inject

class PublishPostUseCase @Inject constructor(
    private val clientManager: ActivityPubClientManager,
    private val uploadMediaAttachment: UploadMediaAttachmentUseCase,
    private val attachmentAdapter: PostStatusAttachmentAdapter,
    private val statusUpdater: StatusUpdater,
    private val statusEntityAdapter: ActivityPubStatusAdapter,
) {

    suspend operator fun invoke(
        account: ActivityPubLoggedAccount,
        content: String,
        attachment: PostStatusAttachment?,
        sensitive: Boolean,
        warningContent: String?,
        visibility: StatusVisibility,
        language: Locale,
        replyToBlogId: String? = null,
        editingBlogId: String? = null,
    ): Result<Unit> {
        val role = IdentityRole(account.uri, null)
        val statusRepo = clientManager.getClient(role).statusRepo
        val medias = handleMedias(role, attachment).let {
            if (it.isFailure) return Result.failure(it.exceptionOrNull()!!)
            it.getOrThrow()
        }
        return if (!editingBlogId.isNullOrEmpty()) {
            statusRepo.editStatus(
                id = editingBlogId,
                status = content,
                mediaIds = medias,
                mediaAttributes = buildMediaAttributes(attachment),
                poll = attachment?.asPollAttachmentOrNull?.let(attachmentAdapter::toPollRequest),
                sensitive = sensitive,
                spoilerText = if (sensitive == true) warningContent else null,
                language = language.isO3LanguageCode,
            )
        } else {
            statusRepo.postStatus(
                status = content,
                mediaIds = medias,
                poll = attachment?.asPollAttachmentOrNull?.let(attachmentAdapter::toPollRequest),
                sensitive = sensitive,
                spoilerText = if (sensitive == true) warningContent else null,
                replyToId = replyToBlogId,
                visibility = visibility.toEntityVisibility(),
                language = language.isO3LanguageCode,
            )
        }.map {
            statusUpdater.update(
                statusEntityAdapter.toStatusUiState(
                    entity = it,
                    platform = account.platform,
                    role = role,
                    loggedAccount = account,
                )
            )
        }
    }

    suspend operator fun invoke(
        account: ActivityPubLoggedAccount,
        uiState: PostStatusUiState,
        editingBlogId: String?,
    ): Result<Unit> {
        return invoke(
            account = account,
            content = uiState.content.text,
            attachment = uiState.attachment,
            sensitive = uiState.sensitive,
            warningContent = uiState.warningContent.text,
            visibility = uiState.visibility,
            language = uiState.language,
            replyToBlogId = uiState.replyToBlog?.id,
            editingBlogId = editingBlogId,
        )
    }

    private suspend fun handleMedias(
        role: IdentityRole,
        attachment: PostStatusAttachment?,
    ): Result<List<String>> {
        if (attachment == null) return Result.success(emptyList())
        val mediaIdList = mutableListOf<String>()
        val localFiles = mutableListOf<PostStatusMediaAttachmentFile.LocalFile>()
        if (attachment is PostStatusAttachment.Image) {
            for (file in attachment.imageList) {
                when (file) {
                    is PostStatusMediaAttachmentFile.LocalFile -> localFiles.add(file)
                    is PostStatusMediaAttachmentFile.RemoteFile -> mediaIdList.add(file.id)
                }
            }
        } else if (attachment is PostStatusAttachment.Video) {
            when (val file = attachment.video) {
                is PostStatusMediaAttachmentFile.LocalFile -> localFiles.add(file)
                is PostStatusMediaAttachmentFile.RemoteFile -> mediaIdList.add(file.id)
            }
        }
        if (localFiles.isNotEmpty()) {
            val uploadResultList = supervisorScope {
                localFiles.map { async { uploadMediaWithAlt(role, it) } }.awaitAll()
            }
            if (uploadResultList.any { it.isFailure }) {
                return Result.failure(uploadResultList.first { it.isFailure }.exceptionOrNull()!!)
            }
            uploadResultList.map { it.getOrThrow() }.forEach { mediaIdList.add(it) }
        }
        return Result.success(mediaIdList)
    }

    private fun buildMediaAttributes(
        attachment: PostStatusAttachment?,
    ): List<ActivityPubEditStatusEntity.MediaAttributes> {
        if (attachment == null) return emptyList()
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
                description = it.alt,
            )
        }
        return mediaAttributes
    }

    private suspend fun uploadMediaWithAlt(
        role: IdentityRole,
        file: PostStatusMediaAttachmentFile.LocalFile,
    ): Result<String> {
        return uploadMediaAttachment(role, file.file)
            .mapCatching { mediaId ->
                if (file.alt.isNullOrEmpty()) {
                    mediaId
                } else {
                    updateAlt(role, mediaId, file.alt).getOrThrow()
                    mediaId
                }
            }
    }

    private suspend fun updateAlt(
        role: IdentityRole,
        fileId: String,
        alt: String?,
    ): Result<Unit> {
        return clientManager.getClient(role).mediaRepo
            .updateMedia(id = fileId, description = alt)
            .map { }
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
