package com.zhangke.fread.bluesky.internal.screen.threaded

import androidx.lifecycle.ViewModel
import app.bsky.feed.GetPostThreadQueryParams
import app.bsky.feed.GetPostThreadResponseThreadUnion
import app.bsky.feed.Post
import app.bsky.feed.PostView
import app.bsky.feed.ThreadViewPost
import app.bsky.feed.ThreadViewPostReplieUnion
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.fread.bluesky.internal.client.BlueskyClientManager
import com.zhangke.fread.bluesky.internal.utils.bskyJson
import com.zhangke.fread.status.model.PlatformLocator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import sh.christian.ozone.api.AtUri

class BlueskyThreadedViewViewModel(
    private val clientManager: BlueskyClientManager,
    private val locator: PlatformLocator,
    private val postUri: String,
    private val opDid: String,
) : ViewModel() {

    private val _uiState = MutableStateFlow<BlueskyThreadedViewUiState>(
        BlueskyThreadedViewUiState.Loading,
    )
    val uiState = _uiState.asStateFlow()

    init {
        launchInViewModel { assemble() }
    }

    private suspend fun assemble() {
        val client = clientManager.getClient(locator)
        val response = client.getPostThreadCatching(
            GetPostThreadQueryParams(uri = AtUri(postUri), depth = THREAD_DEPTH),
        ).getOrNull()
        val thread = (response?.thread as? GetPostThreadResponseThreadUnion.ThreadViewPost)?.value
        if (thread == null) {
            _uiState.value = BlueskyThreadedViewUiState.Empty
            return
        }
        val chain = listOf(thread.post) + longestOpChain(thread.replies, opDid)
        if (chain.size < 2) {
            _uiState.value = BlueskyThreadedViewUiState.Empty
            return
        }
        val text = chain
            .mapNotNull { it.postText() }
            .joinToString("\n\n")
            .takeIf { it.isNotBlank() }
        _uiState.value = if (text == null) {
            BlueskyThreadedViewUiState.Empty
        } else {
            BlueskyThreadedViewUiState.Loaded(text)
        }
    }

    private fun longestOpChain(
        replies: List<ThreadViewPostReplieUnion>,
        opDid: String,
    ): List<PostView> {
        val opReplies: List<ThreadViewPost> = replies
            .mapNotNull { (it as? ThreadViewPostReplieUnion.ThreadViewPost)?.value }
            .filter { it.post.author.did.did == opDid }
        if (opReplies.isEmpty()) return emptyList()
        return opReplies
            .map { reply -> listOf(reply.post) + longestOpChain(reply.replies, opDid) }
            .maxBy { it.size }
    }

    private fun PostView.postText(): String? =
        runCatching {
            val post: Post = record.bskyJson()
            post.text
        }.getOrNull()?.takeIf { it.isNotBlank() }

    companion object {
        private const val THREAD_DEPTH = 20L
    }
}

sealed interface BlueskyThreadedViewUiState {
    data object Loading : BlueskyThreadedViewUiState
    data object Empty : BlueskyThreadedViewUiState
    data class Loaded(val text: String) : BlueskyThreadedViewUiState
}
