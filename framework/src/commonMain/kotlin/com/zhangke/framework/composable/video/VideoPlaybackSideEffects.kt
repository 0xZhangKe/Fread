package com.zhangke.framework.composable.video

import androidx.compose.runtime.Composable

/**
 * Platform-specific lifecycle effects tied to a [VideoPlayerController]:
 *
 *  - Keep the screen on while a video is playing (so it doesn't dim/sleep).
 *  - Request audio focus when audible playback starts so other apps (music,
 *    podcasts) pause; abandon focus when playback stops or the player is
 *    muted.
 *
 * Both behaviors are scoped to the composition that owns the controller, so
 * they are released automatically when the player leaves the screen.
 *
 * No-op on platforms without a notion of audio focus or window flags.
 */
@Composable
expect fun VideoPlaybackSideEffects(controller: VideoPlayerController)
