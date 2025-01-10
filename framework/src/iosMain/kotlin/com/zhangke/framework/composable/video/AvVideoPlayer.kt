package com.zhangke.framework.composable.video

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitView
import com.eygraber.uri.toNSURL
import com.zhangke.framework.utils.PlatformUri
import kotlinx.cinterop.COpaquePointer
import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.cValue
import platform.AVFoundation.AVPlayer
import platform.AVFoundation.AVPlayerItem
import platform.AVFoundation.AVPlayerItemStatusReadyToPlay
import platform.AVFoundation.AVPlayerTimeControlStatusPlaying
import platform.AVFoundation.currentItem
import platform.AVFoundation.currentTime
import platform.AVFoundation.duration
import platform.AVFoundation.pause
import platform.AVFoundation.play
import platform.AVFoundation.replaceCurrentItemWithPlayerItem
import platform.AVFoundation.seekToTime
import platform.AVFoundation.timeControlStatus
import platform.AVFoundation.volume
import platform.AVKit.AVPlayerViewController
import platform.CoreMedia.CMTime
import platform.CoreMedia.CMTimeGetSeconds
import platform.CoreMedia.CMTimeMake
import platform.Foundation.NSKeyValueObservingOptionNew
import platform.Foundation.addObserver
import platform.Foundation.removeObserver
import platform.UIKit.UIView
import platform.darwin.NSObject

@OptIn(ExperimentalForeignApi::class)
class AvVideoPlayer(
    private var avPlayer: AVPlayer?,
) : PlatformVideoPlayer {

    private var avPlayerListener: PlatformVideoPlayerListener? = null

    private var playerObserver: PlayerObserver? = null

    override fun prepare(state: VideoState, playWhenReady: Boolean) {
        avPlayer?.let { player ->
            player.volume = state.playerVolume
            player.currentItem?.addObserver(
                observer = PlayerObserver(
                    onStatusReady = {
                        seekTo(state.targetSeekTo)
                        if (playWhenReady) {
                            play()
                        }
                    },
                ).also {
                    playerObserver = it
                },
                forKeyPath = "status",
                options = NSKeyValueObservingOptionNew,
                context = null,
            )
        }
    }

    override fun play() {
        avPlayer?.play()
    }

    override fun pause() {
        avPlayer?.pause()
    }


    override fun stop() {
        avPlayer?.run {
            pause()
            seekToTime(time = cValue {
                value = 0
            })
        }
    }

    override fun release() {
        avPlayer?.pause()
        playerObserver?.let {
            avPlayer?.removeObserver(it, forKeyPath = "status")
        }
        avPlayer?.replaceCurrentItemWithPlayerItem(null)
        avPlayer = null
    }

    override fun seekTo(position: Long) {
        avPlayer?.seekToTime(
            time = CMTimeMake(position, 1000)
        )
    }

    override var volume: Float
        get() = avPlayer?.volume ?: 0f
        set(value) {
            avPlayer?.volume = value
        }

    override val duration: Long
        get() = avPlayer?.currentItem()?.duration()?.toMillis() ?: 0L

    override val currentPosition: Long
        get() = avPlayer?.currentItem()?.currentTime()?.toMillis() ?: 0L

    override val isPlaying: Boolean
        get() = avPlayer?.timeControlStatus() == AVPlayerTimeControlStatusPlaying

    private fun CValue<CMTime>.toMillis(): Long {
        return CMTimeGetSeconds(this).times(1000.0).toLong()
    }

    @Composable
    override fun Content(modifier: Modifier) {
        UIKitView(
            modifier = modifier,
            factory = {
                val avPlayerViewController = AVPlayerViewController().apply {
                    player = avPlayer
                    showsPlaybackControls = false
                    allowsPictureInPicturePlayback = false
                }
                UIView().apply {
                    addSubview(avPlayerViewController.view)
                }
            },
            update = { _ ->

            },
            // properties = UIKitInteropProperties(
            //     isInteractive = true,
            //     isNativeAccessibilityEnabled = true
            // )
        )
    }

    override fun addListener(listener: PlatformVideoPlayerListener) {
        avPlayerListener = listener
    }

    override fun removeListener(listener: PlatformVideoPlayerListener) {
        avPlayerListener = null
    }

    class Factory : PlatformVideoPlayer.Factory {
        override fun create(uri: PlatformUri): PlatformVideoPlayer {
            return AvVideoPlayer(
                avPlayer = AVPlayer(uri.toNSURL()!!)
            )
        }
    }
}


class PlayerObserver(
    private val onStatusReady: () -> Unit,
) : NSObject() {

    @OptIn(ExperimentalForeignApi::class)
    fun observeValueForKeyPath(
        keyPath: String?,
        ofObject: Any?,
        change: Map<Any?, *>?,
        // context: CPointer<*>?,
        context: COpaquePointer?,
    ) {
        when (keyPath) {
            "status" -> {
                if (ofObject is AVPlayerItem && ofObject.status == AVPlayerItemStatusReadyToPlay) {
                    onStatusReady() // Call callback when ready
                }
            }

            else -> {
                // Handle other key paths or log an error
                println("Unhandled key path: $keyPath")
            }
        }
    }
}