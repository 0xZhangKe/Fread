package com.zhangke.fread.screen

import android.util.Log
import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.animateDecay
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.MotionDurationScale
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.zhangke.fread.common.page.BaseScreen
import com.zhangke.fread.commonbiz.R
import com.zhangke.fread.screen.main.MainPage
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.withContext
import kotlin.math.abs

class FreadScreen : BaseScreen() {

    @Composable
    private fun ScrollDemo() {
        val pagerState = rememberPagerState {
            2
        }
        val connection = remember(pagerState) {
            PagerDefaults.pageNestedScrollConnection(pagerState, Orientation.Horizontal)
        }
        HorizontalPager(
            modifier = Modifier,
            state = pagerState,
            pageNestedScrollConnection = connection,
        ) {
            if (it == 0) {
                val list = remember {
                    List(100) {
                        "Item $it"
                    }
                }
                val flingSpec = rememberSplineBasedDecay<Float>()
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .nestedScroll(connection),
                    flingBehavior = remember {
                        DefaultFlingBehavior(flingSpec)
                    }
                ) {
                    items(list) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                        ) {
                            var expand by remember {
                                mutableStateOf(false)
                            }
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                                    .clickable {
                                        expand = !expand
                                    },
                                text = if (expand) "$it \n $it \n $it" else it,
                            )
                            Image(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                                    .padding(top = 16.dp),
                                painter = painterResource(id = R.drawable.illustration_celebrate),
                                contentDescription = "",
                            )
                        }
                    }
                }
            } else {
                Box(modifier = Modifier.fillMaxSize()) {
                    Text(text = "aaa")
                }
            }
        }
    }


    @OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
    @Composable
    override fun Content() {
        super.Content()
        MainPage()

//        ScrollDemo()
    }
}

private val DefaultScrollMotionDurationScale = object : MotionDurationScale {
    override val scaleFactor: Float
        get() = 1F
}

internal class DefaultFlingBehavior(
    var flingDecay: DecayAnimationSpec<Float>,
    private val motionDurationScale: MotionDurationScale = DefaultScrollMotionDurationScale
) : FlingBehavior {

    // For Testing
    var lastAnimationCycleCount = 0

    override suspend fun ScrollScope.performFling(initialVelocity: Float): Float {
        Log.d("F_TEST", "performFling: $initialVelocity")
        lastAnimationCycleCount = 0
        // come up with the better threshold, but we need it since spline curve gives us NaNs
        return withContext(motionDurationScale) {
            if (abs(initialVelocity) > 1f) {
                var velocityLeft = initialVelocity
                var lastValue = 0f
                val animationState = AnimationState(
                    initialValue = 0f,
                    initialVelocity = initialVelocity,
                )
                try {
                    animationState.animateDecay(flingDecay) {
                        val delta = value - lastValue
                        val consumed = scrollBy(delta)
                        lastValue = value
                        velocityLeft = this.velocity
                        // avoid rounding errors and stop if anything is unconsumed
                        if (abs(delta - consumed) > 0.5f) this.cancelAnimation()
                        lastAnimationCycleCount++
                    }
                } catch (exception: CancellationException) {
                    velocityLeft = animationState.velocity
                } catch (e: kotlin.coroutines.cancellation.CancellationException){
                    velocityLeft = animationState.velocity
                }
                Log.d("F_TEST", "return velocityLeft: $velocityLeft")
                velocityLeft
            } else {
                Log.d("F_TEST", "return initialVelocity: $initialVelocity")
                initialVelocity
            }
        }
    }
}
