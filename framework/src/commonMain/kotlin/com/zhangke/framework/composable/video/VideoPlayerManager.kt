package com.zhangke.framework.composable.video

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.zhangke.framework.utils.Log
import com.zhangke.framework.utils.PlatformUri

val LocalVideoPlayerManager = staticCompositionLocalOf<VideoPlayerManager> {
    error("No VideoPlayerManager provided")
}

@Composable
expect fun rememberVideoPlayerManager(): VideoPlayerManager

class VideoPlayerManager(
    private val factory: PlatformVideoPlayer.Factory,
) {

    private val playerPool = mutableMapOf<PlatformUri, PlatformVideoPlayer>()
    private val uriToLifecycle = mutableMapOf<PlatformUri, Pair<Lifecycle, LifecycleObserver>>()

    fun obtainPlayer(
        uri: PlatformUri,
        lifecycle: Lifecycle,
    ): PlatformVideoPlayer {
        Log.d("PlayerManager") { "obtainPlayer($uri)" }
        return playerPool[uri] ?: createPlayer(uri, lifecycle).also {
            playerPool[uri] = it
        }
    }

    private fun createPlayer(
        uri: PlatformUri,
        lifecycle: Lifecycle,
    ): PlatformVideoPlayer {
        Log.d("PlayerManager") { "createPlayer($uri)" }
        val player = factory.create(uri)
        val observer = object : DefaultLifecycleObserver {

            override fun onStop(owner: LifecycleOwner) {
                super.onStop(owner)
                player.pause()
            }

            override fun onDestroy(owner: LifecycleOwner) {
                super.onDestroy(owner)
                recyclePlayer(uri)
                removeLifecycleObserver(uri)
            }
        }
        lifecycle.addObserver(observer)
        uriToLifecycle[uri] = lifecycle to observer
        return player
    }

    private fun removeLifecycleObserver(uri: PlatformUri) {
        uriToLifecycle.remove(uri)?.also {
            it.first.removeObserver(it.second)
        }
    }

    fun recyclePlayer(uri: PlatformUri) {
        Log.d("PlayerManager") { "recyclePlayer($uri)" }
        removeLifecycleObserver(uri)
        val player = playerPool.remove(uri) ?: return
        // player.setVideoSurfaceView(null)
        player.stop()
        player.release()
        Log.d("PlayerManager") { "recyclePlayer($uri) finished" }
    }

    fun recycler() {
        playerPool.forEach {
            recyclePlayer(it.key)
        }
    }
}
