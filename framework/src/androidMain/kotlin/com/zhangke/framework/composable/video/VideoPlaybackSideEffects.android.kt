package com.zhangke.framework.composable.video

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
actual fun VideoPlaybackSideEffects(controller: VideoPlayerController) {
    KeepScreenOnWhilePlaying(controller)
    RequestAudioFocusWhilePlaying(controller)
}

/** Sets the hosting view's `keepScreenOn` while [controller] is playing. */
@Composable
private fun KeepScreenOnWhilePlaying(controller: VideoPlayerController) {
    val view = LocalView.current
    LaunchedEffect(controller, view) {
        snapshotFlow { controller.isPlaying }
            .distinctUntilChanged()
            .collectLatest { playing -> view.keepScreenOn = playing }
    }
    DisposableEffect(view) {
        onDispose { view.keepScreenOn = false }
    }
}

/**
 * Requests `AUDIOFOCUS_GAIN` while the controller is playing audibly (playing
 * AND not muted), causing other apps' music/podcast playback to pause. Focus
 * is abandoned as soon as the video pauses, is muted, or leaves composition.
 *
 * No focus listener is registered — losing focus mid-playback (incoming call
 * etc.) does not auto-pause the video. That matches the existing behavior;
 * the goal here is only to stop other apps' audio.
 */
@Composable
private fun RequestAudioFocusWhilePlaying(controller: VideoPlayerController) {
    val context = LocalContext.current
    LaunchedEffect(controller, context) {
        val audioManager =
            context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
                ?: return@LaunchedEffect
        val focusGate = AudioFocusGate(audioManager)
        try {
            snapshotFlow { controller.isPlaying && !controller.isMuted }
                .distinctUntilChanged()
                .collectLatest { wantFocus ->
                    if (wantFocus) focusGate.request() else focusGate.abandon()
                }
        } finally {
            focusGate.abandon()
        }
    }
}

/**
 * Thin wrapper over the two audio-focus APIs (pre-O streamType/listener form
 * vs. O+ `AudioFocusRequest`). Holds at most one focus grant at a time.
 */
private class AudioFocusGate(private val audioManager: AudioManager) {
    private var holding = false
    private val legacyListener = AudioManager.OnAudioFocusChangeListener { /* no-op */ }
    private val focusRequest: AudioFocusRequest? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MOVIE)
                        .build()
                )
                .setOnAudioFocusChangeListener(legacyListener)
                .build()
        } else {
            null
        }

    fun request() {
        if (holding) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && focusRequest != null) {
            audioManager.requestAudioFocus(focusRequest)
        } else {
            @Suppress("DEPRECATION")
            audioManager.requestAudioFocus(
                legacyListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN,
            )
        }
        holding = true
    }

    fun abandon() {
        if (!holding) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && focusRequest != null) {
            audioManager.abandonAudioFocusRequest(focusRequest)
        } else {
            @Suppress("DEPRECATION")
            audioManager.abandonAudioFocus(legacyListener)
        }
        holding = false
    }
}
