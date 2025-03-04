package com.zhangke.fread.bluesky.internal.screen.publish

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.bsky.embed.AspectRatio
import app.bsky.embed.Images
import app.bsky.embed.ImagesImage
import app.bsky.embed.Record
import app.bsky.embed.RecordWithMedia
import app.bsky.embed.RecordWithMediaMediaUnion
import app.bsky.embed.Video
import app.bsky.feed.GetPostThreadQueryParams
import app.bsky.feed.GetPostThreadResponseThreadUnion
import app.bsky.feed.Post
import app.bsky.feed.PostEmbedUnion
import app.bsky.feed.PostReplyRef
import app.bsky.feed.PostView
import app.bsky.feed.Postgate
import app.bsky.feed.PostgateDisableRule
import app.bsky.feed.PostgateEmbeddingRuleUnion
import app.bsky.feed.Threadgate
import app.bsky.feed.ThreadgateAllowUnion
import app.bsky.feed.ThreadgateFollowerRule
import app.bsky.feed.ThreadgateFollowingRule
import app.bsky.feed.ThreadgateListRule
import app.bsky.feed.ThreadgateMentionRule
import com.atproto.repo.ApplyWritesCreate
import com.atproto.repo.ApplyWritesRequest
import com.atproto.repo.ApplyWritesRequestWriteUnion
import com.atproto.repo.StrongRef
import com.zhangke.framework.architect.json.fromJson
import com.zhangke.framework.architect.json.globalJson
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.emitTextMessageFromThrowable
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.utils.ContentProviderFile
import com.zhangke.framework.utils.PlatformUri
import com.zhangke.fread.bluesky.internal.client.BlueskyClientManager
import com.zhangke.fread.bluesky.internal.client.BskyCollections
import com.zhangke.fread.bluesky.internal.client.adjustToRkey
import com.zhangke.fread.bluesky.internal.model.ReplySetting
import com.zhangke.fread.bluesky.internal.usecase.GetAllListsUseCase
import com.zhangke.fread.bluesky.internal.usecase.UploadBlobUseCase
import com.zhangke.fread.bluesky.internal.utils.Tid
import com.zhangke.fread.bluesky.internal.utils.bskyJson
import com.zhangke.fread.common.config.FreadConfigManager
import com.zhangke.fread.common.di.ViewModelFactory
import com.zhangke.fread.common.utils.PlatformUriHelper
import com.zhangke.fread.status.blog.Blog
import com.zhangke.fread.status.model.IdentityRole
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.datetime.Clock
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import sh.christian.ozone.api.AtUri
import sh.christian.ozone.api.Cid
import sh.christian.ozone.api.Did
import sh.christian.ozone.api.Language
import sh.christian.ozone.api.RKey

class PublishPostViewModel @Inject constructor(
    private val clientManager: BlueskyClientManager,
    private val getAllLists: GetAllListsUseCase,
    private val platformUriHelper: PlatformUriHelper,
    private val configManager: FreadConfigManager,
    private val uploadBlob: UploadBlobUseCase,
    @Assisted private val role: IdentityRole,
    @Assisted replyBlogJsonString: String?,
    @Assisted quoteBlogJsonString: String?,
) : ViewModel() {

    fun interface Factory : ViewModelFactory {

        fun create(
            role: IdentityRole,
            replyBlogJsonString: String?,
            quoteBlogJsonString: String?,
        ): PublishPostViewModel
    }

    private val _uiState = MutableStateFlow(PublishPostUiState.default())
    val uiState = _uiState.asStateFlow()

    private val _snackBarMessageFlow = MutableSharedFlow<TextString>()
    val snackBarMessageFlow = _snackBarMessageFlow.asSharedFlow()

    private val _finishPageFlow = MutableSharedFlow<Unit>()
    val finishPageFlow = _finishPageFlow.asSharedFlow()

    private var publishJob: Job? = null

    init {
        launchInViewModel(Dispatchers.IO) {
            val reply: Blog? = replyBlogJsonString?.let {
                globalJson.fromJson<Blog>(it)
            }
            val quote: Blog? = quoteBlogJsonString?.let {
                globalJson.fromJson<Blog>(it)
            }
            _uiState.update { it.copy(replyBlog = reply, quoteBlog = quote) }
        }
        launchInViewModel {
            val client = clientManager.getClient(role)
            val account = client.loggedAccountProvider() ?: return@launchInViewModel
            _uiState.update { it.copy(account = account) }
        }
        launchInViewModel {
            configManager.getBskyPublishLanguage().let {
                _uiState.update { state ->
                    val selectedLanguages = it.ifEmpty {
                        listOf("en")
                    }
                    state.copy(selectedLanguages = selectedLanguages)
                }
            }
        }
        loadUserList()
    }

    fun onContentChanged(text: TextFieldValue) {
        _uiState.update { it.copy(content = text) }
    }

    fun onQuoteChange(allowQuote: Boolean) {
        _uiState.update {
            it.copy(interactionSetting = it.interactionSetting.copy(allowQuote = allowQuote))
        }
    }

    fun onReplySettingChange(replySetting: ReplySetting) {
        _uiState.update {
            it.copy(
                interactionSetting = it.interactionSetting.copy(replySetting = replySetting)
            )
        }
    }

    fun onSettingOptionsSelected(option: ReplySetting.CombineOption) {
        _uiState.update { state ->
            val options = state.interactionSetting.replySetting.let { it as? ReplySetting.Combined }
                ?.options?.toMutableList() ?: mutableListOf()
            if (option in options) {
                options.remove(option)
            } else {
                options.add(option)
            }
            state.copy(
                interactionSetting = state.interactionSetting.copy(
                    replySetting = ReplySetting.Combined(options),
                )
            )
        }
    }

    fun onMediaSelected(medias: List<PlatformUri>) {
        if (medias.isEmpty()) return
        viewModelScope.launch {
            val currentAttachment = uiState.value.attachment
            val fileList = medias.map {
                async { platformUriHelper.read(it) }
            }.awaitAll().filterNotNull()
            val attachment = if (fileList.first().isVideo) {
                PublishPostMediaAttachment.Video(fileList.first().toUiFile())
            } else {
                PublishPostMediaAttachment.Image(fileList.map { it.toUiFile() })
            }
            _uiState.update { it.copy(attachment = currentAttachment.merge(attachment)) }
        }
    }

    private fun PublishPostMediaAttachment?.merge(
        attachment: PublishPostMediaAttachment
    ): PublishPostMediaAttachment {
        if (this == null) return attachment
        if (this is PublishPostMediaAttachment.Video || attachment is PublishPostMediaAttachment.Video) {
            return attachment
        }
        val files = (this as PublishPostMediaAttachment.Image).files
        return PublishPostMediaAttachment.Image(files + (attachment as PublishPostMediaAttachment.Image).files)
    }

    private fun ContentProviderFile.toUiFile(): PublishPostMediaAttachmentFile {
        return PublishPostMediaAttachmentFile(
            file = this,
            alt = null
        )
    }

    fun onMediaAltChanged(file: PublishPostMediaAttachmentFile, alt: String) {
        val attachment = uiState.value.attachment
        if (attachment is PublishPostMediaAttachment.Video) {
            _uiState.update {
                it.copy(attachment = PublishPostMediaAttachment.Video(file.copy(alt = alt)))
            }
        } else {
            val files = (attachment as PublishPostMediaAttachment.Image).files.map {
                if (it == file) {
                    it.copy(alt = alt)
                } else {
                    it
                }
            }
            _uiState.update { it.copy(attachment = PublishPostMediaAttachment.Image(files)) }
        }
    }

    fun onMediaDeleteClick(file: PublishPostMediaAttachmentFile) {
        val attachment = uiState.value.attachment
        if (attachment is PublishPostMediaAttachment.Video) {
            _uiState.update { it.copy(attachment = null) }
        } else {
            val files = (attachment as PublishPostMediaAttachment.Image).files.filter { it != file }
            _uiState.update { it.copy(attachment = PublishPostMediaAttachment.Image(files)) }
        }
    }

    fun onLanguageSelected(selectedLanguages: List<String>) {
        launchInViewModel {
            configManager.updateBskyPublishLanguage(selectedLanguages)
        }
        _uiState.update { it.copy(selectedLanguages = selectedLanguages) }
    }

    fun onPublishClick() {
        if (publishJob?.isActive == true) return
        publishJob?.cancel()
        val account = uiState.value.account ?: return
        publishJob = launchInViewModel {
            _uiState.update { it.copy(publishing = true) }
            val client = clientManager.getClient(role)
            val rkey = Tid.generateTID()
            val postUri = "at://${account.did}/${BskyCollections.feedPost.nsid}/$rkey"
            val embedResult = buildPostEmbed()
            if (embedResult.isFailure) {
                _uiState.update { it.copy(publishing = false) }
                _snackBarMessageFlow.emitTextMessageFromThrowable(embedResult.exceptionOrNull()!!)
                return@launchInViewModel
            }
            val replyResult = buildReplyRef()
            if (replyResult.isFailure) {
                _uiState.update { it.copy(publishing = false) }
                _snackBarMessageFlow.emitTextMessageFromThrowable(replyResult.exceptionOrNull()!!)
                return@launchInViewModel
            }
            val post = Post(
                text = uiState.value.content.text,
                langs = uiState.value.selectedLanguages.map { Language(it) },
                embed = embedResult.getOrNull(),
                createdAt = Clock.System.now(),
                reply = replyResult.getOrNull(),
            )
            val writes = mutableListOf<ApplyWritesRequestWriteUnion>()
            writes += ApplyWritesRequestWriteUnion.Create(
                ApplyWritesCreate(
                    collection = BskyCollections.feedPost,
                    value = post.bskyJson(),
                    rkey = RKey(rkey),
                )
            )
            writes += buildTreadAndPostGate(AtUri(postUri))
            val request = ApplyWritesRequest(
                repo = Did(account.did),
                validate = true,
                writes = writes,
            )
            client.applyWritesCatching(request)
                .onFailure { t ->
                    _uiState.update { it.copy(publishing = false) }
                    _snackBarMessageFlow.emitTextMessageFromThrowable(t)
                }.onSuccess {
                    _uiState.update { it.copy(publishing = false) }
                    _finishPageFlow.emit(Unit)
                }
        }
    }

    private suspend fun buildReplyRef(): Result<PostReplyRef?> {
        val reply = uiState.value.replyBlog ?: return Result.success(null)
        val postView = getPostDetail(reply.url).let {
            if (it.isFailure) return Result.failure(it.exceptionOrNull()!!)
            it.getOrThrow()
        }
        val post: Post = postView.record.bskyJson()
        val replyPostRef = StrongRef(uri = postView.uri, cid = postView.cid)
        val root = post.reply?.root ?: replyPostRef
        return Result.success(
            PostReplyRef(root = root, parent = replyPostRef)
        )
    }

    private suspend fun getPostDetail(uri: String): Result<PostView> {
        val client = clientManager.getClient(role)
        val result = client.getPostThreadCatching(
            GetPostThreadQueryParams(uri = AtUri(uri), depth = 1)
        )
        if (result.isFailure) return Result.failure(result.exceptionOrNull()!!)
        val response = result.getOrThrow()
        val threadPostView = (response.thread as? GetPostThreadResponseThreadUnion.ThreadViewPost)
            ?: return Result.failure(IllegalStateException("Post not found"))
        return Result.success(threadPostView.value.post)
    }

    private fun buildTreadAndPostGate(postUri: AtUri): List<ApplyWritesRequestWriteUnion> {
        val list = mutableListOf<ApplyWritesRequestWriteUnion>()
        val interactionSetting = uiState.value.interactionSetting
        val replySetting = interactionSetting.replySetting
        if (replySetting !is ReplySetting.Everybody) {
            val allowList = mutableListOf<ThreadgateAllowUnion>()
            if (replySetting is ReplySetting.Combined) {
                allowList += buildThreadGateAllowList(replySetting)
            }
            val threadGate = Threadgate(
                post = postUri,
                allow = allowList,
                createdAt = Clock.System.now(),
            )
            list += ApplyWritesRequestWriteUnion.Create(
                ApplyWritesCreate(
                    collection = BskyCollections.threadGate,
                    value = threadGate.bskyJson(),
                    rkey = postUri.toString().adjustToRkey(),
                )
            )
        }
        if (!interactionSetting.allowQuote) {
            val postGate = Postgate(
                createdAt = Clock.System.now(),
                post = postUri,
                embeddingRules = listOf(PostgateEmbeddingRuleUnion.DisableRule(PostgateDisableRule)),
            )
            list += ApplyWritesRequestWriteUnion.Create(
                ApplyWritesCreate(
                    collection = BskyCollections.postGate,
                    value = postGate.bskyJson(),
                    rkey = postUri.toString().adjustToRkey(),
                )
            )
        }
        return list
    }

    private fun buildThreadGateAllowList(
        setting: ReplySetting.Combined,
    ): List<ThreadgateAllowUnion> {
        return buildList {
            setting.options.forEach { option ->
                when (option) {
                    is ReplySetting.CombineOption.Mentioned -> {
                        add(ThreadgateAllowUnion.MentionRule(ThreadgateMentionRule))
                    }

                    is ReplySetting.CombineOption.Following -> {
                        add(ThreadgateAllowUnion.FollowingRule(ThreadgateFollowingRule))
                    }

                    is ReplySetting.CombineOption.Followers -> {
                        add(ThreadgateAllowUnion.FollowerRule(ThreadgateFollowerRule))
                    }

                    is ReplySetting.CombineOption.UserInList -> {
                        add(ThreadgateAllowUnion.ListRule(ThreadgateListRule(option.listView.uri)))
                    }
                }
            }
        }
    }

    private suspend fun buildPostEmbed(): Result<PostEmbedUnion?> {
        val attachment = uiState.value.attachment
        val videoResult =
            (attachment as? PublishPostMediaAttachment.Video)?.let { uploadVideo(it.file) }
        if (videoResult?.isFailure == true) return Result.failure(videoResult.exceptionOrNull()!!)
        val imagesResult =
            (attachment as? PublishPostMediaAttachment.Image)?.let { uploadImages(it) }
        if (imagesResult?.isFailure == true) return Result.failure(imagesResult.exceptionOrNull()!!)
        val video = videoResult?.getOrNull()
        val images = imagesResult?.getOrNull()
        val quoteRecord = uiState.value.quoteBlog
            ?.let { StrongRef(uri = AtUri(it.url), cid = Cid(it.id)) }
            ?.let { Record(it) }
        if (video == null && images == null && quoteRecord == null) return Result.success(null)
        val embed = if (quoteRecord != null) {
            if (video != null || images != null) {
                val media = video?.let { RecordWithMediaMediaUnion.Video(it) }
                    ?: RecordWithMediaMediaUnion.Images(images!!)
                PostEmbedUnion.RecordWithMedia(
                    RecordWithMedia(
                        record = quoteRecord,
                        media = media,
                    )
                )
            } else {
                PostEmbedUnion.Record(quoteRecord)
            }
        } else {
            video?.let { PostEmbedUnion.Video(it) } ?: PostEmbedUnion.Images(images!!)
        }
        return Result.success(embed)
    }

    private fun buildQuoteEmbed(): StrongRef? {
        val quoteBlog = uiState.value.quoteBlog ?: return null
        return StrongRef(uri = AtUri(quoteBlog.url), cid = Cid(quoteBlog.id))
    }

    private suspend fun uploadVideo(file: PublishPostMediaAttachmentFile): Result<Video> {
        return uploadBlob(role = role, fileUri = file.file.uri)
            .map {
                Video(
                    video = it.first,
                    alt = file.alt,
                    aspectRatio = it.second?.convert(),
                )
            }
    }

    private suspend fun uploadImages(image: PublishPostMediaAttachment.Image): Result<Images> {
        val resultList: List<Result<ImagesImage>> = supervisorScope {
            image.files.map { file ->
                async {
                    uploadBlob(role = role, fileUri = file.file.uri).map {
                        ImagesImage(
                            image = it.first,
                            aspectRatio = it.second?.convert(),
                            alt = file.alt.orEmpty(),
                        )
                    }
                }
            }.awaitAll()
        }
        if (resultList.any { it.isFailure }) {
            return Result.failure(resultList.first { it.isFailure }.exceptionOrNull()!!)
        }
        val images = Images(resultList.map { it.getOrThrow() })
        return Result.success(images)
    }

    private fun com.zhangke.framework.utils.AspectRatio.convert(): AspectRatio {
        return AspectRatio(
            width = width,
            height = height,
        )
    }

    private fun loadUserList() {
        launchInViewModel {
            val client = clientManager.getClient(role)
            val account = client.loggedAccountProvider() ?: return@launchInViewModel
            getAllLists(role, Did(account.did))
                .onSuccess { lists -> _uiState.update { it.copy(list = lists) } }
        }
    }
}
