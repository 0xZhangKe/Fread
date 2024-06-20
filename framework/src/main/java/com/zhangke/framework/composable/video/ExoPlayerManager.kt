package com.zhangke.framework.composable.video

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.annotation.OptIn
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.zhangke.framework.utils.toMediaSource

val LocalExoPlayerManager = staticCompositionLocalOf { ExoPlayerManager() }

class ExoPlayerManager {

    private val playerPool = mutableMapOf<Uri, ExoPlayer>()
    private val uriToLifecycle = mutableMapOf<Uri, Pair<Lifecycle, LifecycleObserver>>()

    fun obtainPlayer(
        context: Context,
        uri: Uri,
        lifecycle: Lifecycle,
    ): ExoPlayer {
        Log.d("PlayerManager", "obtainPlayer($uri)")
        return playerPool[uri] ?: createPlayer(context, uri, lifecycle).also { playerPool[uri] = it }
    }

    @OptIn(UnstableApi::class)
    private fun createPlayer(context: Context, uri: Uri, lifecycle: Lifecycle): ExoPlayer {
        Log.d("PlayerManager", "createPlayer($uri)")
        val player = ExoPlayer.Builder(context).build()
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

    private fun removeLifecycleObserver(uri: Uri) {
        uriToLifecycle.remove(uri)?.also {
            it.first.removeObserver(it.second)
        }
    }

    fun recyclePlayer(uri: Uri) {
        Log.d("PlayerManager", "recyclePlayer($uri)")
        removeLifecycleObserver(uri)
        val player = playerPool.remove(uri) ?: return
        player.setVideoSurfaceView(null)
        player.stop()
        player.release()
        Log.d("PlayerManager", "recyclePlayer($uri) finished")
    }

    fun recycler(){
        playerPool.forEach {
            recyclePlayer(it.key)
        }
    }
}
