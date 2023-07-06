package com.zhangke.utopia.activitypubapp.screen.service

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionScene
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.androidx.AndroidScreen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil.compose.AsyncImage
import com.zhangke.framework.composable.ToolbarTokens
import com.zhangke.framework.composable.requireSuccessData

class ServerDetailScreen(
    private val host: String,
) : AndroidScreen() {

    @Composable
    override fun Content() {
        val viewModel: ServerDetailViewModel = viewModel()
        val uiState by viewModel.uiState.collectAsState()
        val navigator = LocalNavigator.currentOrThrow

        ServiceDetailContent(
            uiState = uiState.requireSuccessData(),
            onBackClick = navigator::pop,
        )
    }

    @OptIn(ExperimentalMotionApi::class)
    @Composable
    private fun ServiceDetailContent(
        uiState: ServerDetailUiState,
        onBackClick: () -> Unit,
    ) {
        var progress by remember {
            mutableStateOf(0F)
        }
        val motionScene = MotionScene {
            val backIcon = createRefFor("backIcon")
            val toolbarPlaceholder = createRefFor("toolbarPlaceholder")
            val banner = createRefFor("banner")
            defaultTransition(
                from = constraintSet {
                    constrain(backIcon) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                        customColor("color", Color(0xffffffff))
                    }
                    constrain(toolbarPlaceholder) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                        alpha = 0F
                    }
                    constrain(banner){
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                    }
                },
                to = constraintSet {
                    constrain(backIcon) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                        customColor("color", Color(0xFF000000))
                    }
                    constrain(toolbarPlaceholder) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                        alpha = 1F
                    }
                    constrain(banner){
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                    }
                }
            )
        }
        Box(modifier = Modifier.fillMaxSize()) {
            MotionLayout(
                modifier = Modifier.fillMaxWidth(),
                motionScene = motionScene,
                progress = progress,
            ) {
                val backIconProperties = motionProperties(id = "backIcon")
                AsyncImage(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.777F)
                        .layoutId("banner"),
                    model = uiState.thumbnail,
                    contentDescription = "Thumbnail",
                )
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(ToolbarTokens.ContainerHeight)
                        .background(Color.White)
                        .layoutId("toolbarPlaceholder"),
                    shadowElevation = 2.dp,
                ) {}
                Box(
                    modifier = Modifier
                        .height(ToolbarTokens.ContainerHeight)
                        .padding(start = ToolbarTokens.TopAppBarHorizontalPadding)
                        .layoutId("backIcon"),
                    contentAlignment = Alignment.Center,
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            modifier = Modifier.size(ToolbarTokens.LeadingIconSize),
                            painter = rememberVectorPainter(Icons.Default.ArrowBack),
                            contentDescription = "back",
                            tint = backIconProperties.value.color("color"),
                        )
                    }
                }
//
//                val properties = motionProperties(id = "toolbar")
//
//                Box(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(48.dp)
//                ) {
//                    IconButton(
//                        onClick = onBackClick,
//                    ) {
//                        Icon(
//                            painter = rememberVectorPainter(Icons.Default.ArrowBack),
//                            contentDescription = "Back",
//                        )
//                    }
//                }
            }

            Slider(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 32.dp),
                value = progress,
                onValueChange = {
                    progress = it
                },
            )
        }
    }

    @OptIn(ExperimentalMotionApi::class)
    @Composable
    private fun buildServerScreenScene(): String {

        return """
                {
                  ConstraintSets: {
                    start: {
                      backIcon: {
                        custom: {
                          color: '#FFFFFF'
                        }
                      },
                      toolbar: {
                        alpha: 0F,
                      }
                    },
                    end: {
                      backIcon: {
                        custom: {
                          color: '#000000'
                        },
                        toolbar: {
                          alpha: 1F,
                        }
                      }
                    }
                  }
                }
        """.trimIndent()
    }
}
