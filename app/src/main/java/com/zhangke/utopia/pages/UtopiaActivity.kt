package com.zhangke.utopia.pages

import android.graphics.Color
import android.graphics.ColorSpace
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.ui.unit.dp
import androidx.core.view.marginTop
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.bottomSheet.BottomSheetNavigator
import com.zhangke.framework.architect.theme.UtopiaTheme
import com.zhangke.framework.voyager.TransparentNavigator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UtopiaActivity : AppCompatActivity() {

    @OptIn(ExperimentalMaterialApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(createContentView())
//        setContent {
//            UtopiaTheme {
//                TransparentNavigator {
//                    BottomSheetNavigator(
//                        sheetShape = RoundedCornerShape(12.dp),
//                    ) {
//                        Navigator(UtopiaScreen())
//                    }
//                }
//            }
//        }
    }

    private fun createContentView(): View {
        val container = LinearLayout(this).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_VERTICAL
        }

        container.addView(
            createInlineItemView(
//                "https://video.twimg.com/ext_tw_video/1712110948700352512/pu/vid/avc1/720x1280/i43wruptl2R9KHAZ.mp4?tag=12",
                "https://video.twimg.com/ext_tw_video/1718975693181100033/pu/vid/avc1/480x852/QlSJulSIWV3MnXV_.mp4?tag=12",
                true
            )
        )

        container.addView(View(this).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                6
            )
            setBackgroundColor(Color.BLUE)
        })

        container.addView(View(this).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                300
            )
        })

        container.addView(
            createInlineItemView(
                "https://video.twimg.com/ext_tw_video/1712110948700352512/pu/vid/avc1/720x1280/i43wruptl2R9KHAZ.mp4?tag=12",
                true
            )
        )
        return container
    }

    private fun createInlineItemView(
        uri: String,
        playWhenReady: Boolean,
    ): View {
        return FrameLayout(this).apply {
            layoutParams = ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            ).apply {
                marginStart = 40
                marginEnd = 40
                topMargin = 100
                bottomMargin = 20
            }
            addView(createInlineVideoView(uri, playWhenReady))
        }
    }

    @androidx.annotation.OptIn(UnstableApi::class)
    private fun createInlineVideoView(
        uri: String,
        playWhenReady: Boolean,
    ): View {
//        Log.d("U_TEST", "createInlineVideoView($playWhenReady)")
        val exoPlayer = ExoPlayer.Builder(this)
            .build()
            .apply {
                val defaultDataSourceFactory = DefaultDataSource.Factory(this@UtopiaActivity)
                val dataSourceFactory =
                    DefaultDataSource.Factory(this@UtopiaActivity, defaultDataSourceFactory)
                val source = ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(MediaItem.fromUri(uri))
                setMediaSource(source)
                prepare()
                Log.d("U_TEST", "source:$source, player:$this")
                this.playWhenReady = playWhenReady
                videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
                repeatMode = Player.REPEAT_MODE_OFF
            }
        return PlayerView(this).apply {
            resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
            useController = false
            controllerAutoShow = false
            player = exoPlayer
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                400,
            )
        }
    }
}
