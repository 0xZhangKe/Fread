package com.zhangke.framework.composable.video

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitView
import com.eygraber.uri.toNSURL
import com.zhangke.framework.kvo.addObserverSingle
import com.zhangke.framework.kvo.observeKeyValueAsFlow
import com.zhangke.framework.utils.PlatformUri
import com.zhangke.framework.utils.toDuration
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import platform.AVFoundation.AVLayerVideoGravityResize
import platform.AVFoundation.AVLayerVideoGravityResizeAspect
import platform.AVFoundation.AVLayerVideoGravityResizeAspectFill
import platform.AVFoundation.AVPlayer
import platform.AVFoundation.AVPlayerItemDidPlayToEndTimeNotification
import platform.AVFoundation.AVPlayerItemStatusReadyToPlay
import platform.AVFoundation.AVPlayerItemStatusUnknown
import platform.AVFoundation.AVPlayerStatusFailed
import platform.AVFoundation.AVPlayerTimeControlStatusPlaying
import platform.AVFoundation.AVPlayerTimeControlStatusWaitingToPlayAtSpecifiedRate
import platform.AVFoundation.currentItem
import platform.AVFoundation.currentTime
import platform.AVFoundation.duration
import platform.AVFoundation.pause
import platform.AVFoundation.play
import platform.AVFoundation.replaceCurrentItemWithPlayerItem
import platform.AVFoundation.seekToTime
import platform.AVFoundation.volume
import platform.AVKit.AVPlayerViewController
import platform.CoreMedia.CMTimeGetSeconds
import platform.CoreMedia.CMTimeMake
import platform.CoreMedia.CMTimeMakeWithSeconds
import platform.Foundation.NSKeyValueObservingOptionInitial
import platform.Foundation.NSKeyValueObservingOptionNew
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSOperationQueue
import platform.Foundation.removeObserver
import platform.UIKit.UIView
import platform.darwin.NSObject
import kotlin.time.Duration

@OptIn(ExperimentalForeignApi::class)
class AvVideoPlayerController(
    private var avPlayer: AVPlayer,
) : VideoPlayerController {

    override val volumeFlow: Flow<Float> = avPlayer
        .observeKeyValueAsFlow(
            "volume",
            NSKeyValueObservingOptionInitial or NSKeyValueObservingOptionNew,
        )

    private val _isLoadingFlow = MutableStateFlow(false)
    override val isPlayingFlow: Flow<Boolean> = _isLoadingFlow.asStateFlow()

    private val _isPlayingFlow = MutableStateFlow(false)
    override val isLoadingFlow: Flow<Boolean> = _isPlayingFlow.asStateFlow()

    private val _hasVideoEndedFlow = MutableStateFlow(false)
    override val hasVideoEndedFlow: Flow<Boolean> = _hasVideoEndedFlow.asStateFlow()

    private val _errorFlow = MutableSharedFlow<String>()
    override val errorFlow: Flow<String> = _errorFlow.asSharedFlow()

    override val duration: Duration
        get() = avPlayer.currentItem()?.duration()?.toDuration() ?: Duration.ZERO

    override val currentPosition: Duration
        get() = avPlayer.currentItem()?.currentTime()?.toDuration() ?: Duration.ZERO

    private var timeControlStatusObserver: NSObject? = null
    private var statusObserver: NSObject? = null
    private var playbackEndObserver: Any? = null

    override fun prepare(state: VideoState, playWhenReady: Boolean) {
        avPlayer.volume = state.playerVolume

        timeControlStatusObserver = avPlayer.addObserverSingle<Long>(
            "timeControlStatus",
            options = NSKeyValueObservingOptionInitial or NSKeyValueObservingOptionNew,
        ) { timeControlStatus ->
            _isLoadingFlow.value = timeControlStatus == AVPlayerTimeControlStatusWaitingToPlayAtSpecifiedRate
            _isPlayingFlow.value = timeControlStatus == AVPlayerTimeControlStatusPlaying
        }
        statusObserver = avPlayer.currentItem!!.addObserverSingle<Long>(
            "status",
            options = NSKeyValueObservingOptionInitial or NSKeyValueObservingOptionNew,
        ) { status ->
            when (status) {
                AVPlayerItemStatusReadyToPlay -> {
                    seekTo(state.targetSeekTo)
                    if (playWhenReady) {
                        play()
                    }
                }
                AVPlayerItemStatusUnknown -> {
                    _errorFlow.tryEmit("Unknown to prepare video player")
                }
                AVPlayerStatusFailed -> {
                    _errorFlow.tryEmit("Failed to prepare video player")
                }
            }
        }

        _hasVideoEndedFlow.value = false
        playbackEndObserver = NSNotificationCenter.defaultCenter.addObserverForName(
            name = AVPlayerItemDidPlayToEndTimeNotification,
            `object` = avPlayer.currentItem,
            queue = NSOperationQueue.mainQueue,
            usingBlock = {
                _hasVideoEndedFlow.value = true
            }
        )
    }

    override fun play() {
        avPlayer.play()
    }

    override fun pause() {
        avPlayer.pause()
    }

    override fun stop() {
        avPlayer.pause()
        avPlayer.seekToTime(CMTimeMake(0, 1000))
        avPlayer.replaceCurrentItemWithPlayerItem(null)
    }

    override fun release() {
        avPlayer.pause()
        timeControlStatusObserver?.let { avPlayer.removeObserver(it, "timeControlStatus") }
        statusObserver?.let { avPlayer.currentItem?.removeObserver(it, "status") }
        avPlayer.replaceCurrentItemWithPlayerItem(null)
        avPlayer.finalize()
        playbackEndObserver?.let { NSNotificationCenter.defaultCenter.removeObserver(it) }
    }

    override fun seekTo(position: Long) {
        if (currentPosition.inWholeMilliseconds == position) {
            return
        }
        avPlayer.seekToTime(
            time = CMTimeMakeWithSeconds(position.toDouble() / 1000, preferredTimescale = 1000)
        )
    }

    override fun setVolume(volume: Float) {
        avPlayer.volume = volume
    }

    @Composable
    override fun Content(
        aspectRatio: VideoAspectRatio,
        modifier: Modifier,
    ) {
        UIKitView(
            modifier = modifier,
            factory = {
                val avPlayerViewController = AVPlayerViewController().apply {
                    player = avPlayer
                    videoGravity = when (aspectRatio) {
                        VideoAspectRatio.ScaleToFit -> AVLayerVideoGravityResizeAspect
                        VideoAspectRatio.ScaleToFill -> AVLayerVideoGravityResizeAspectFill
                        VideoAspectRatio.FillStretch -> AVLayerVideoGravityResize
                    }
                    showsPlaybackControls = false
                }
                avPlayerViewController.view
            },
        )
    }

    class Factory : VideoPlayerController.Factory {
        override fun create(uri: PlatformUri): VideoPlayerController {
            return AvVideoPlayerController(
                avPlayer = AVPlayer(uri.toNSURL()!!)
            )
        }
    }
}
