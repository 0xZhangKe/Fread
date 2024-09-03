package com.zhangke.fread.status.ui.image

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import com.zhangke.framework.ktx.averageDropFirst
import com.zhangke.framework.ktx.second
import com.zhangke.framework.ktx.third
import com.zhangke.framework.utils.pxToDp

@Composable
internal fun FivefoldImageMediaLayout(
    modifier: Modifier,
    containerWidth: Dp,
    aspectList: List<Float>,
    style: BlogImageMediaStyle,
    itemContent: @Composable (index: Int) -> Unit,
) {
    val density = LocalDensity.current
    val firstAspect = aspectList.first()
    if (firstAspect < 1F) {
        HorizontalImageMediaFrameLayout(
            modifier = modifier,
            containerWidth = containerWidth,
            style = style,
            startAspect = firstAspect,
            startContent = {
                var mainlyWidth: Dp? by remember {
                    mutableStateOf(null)
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .onGloballyPositioned {
                            mainlyWidth = it.size.width.pxToDp(density)
                        }
                ) {
                    if (mainlyWidth != null) {
                        val verticalList = remember {
                            mutableListOf<Float>().apply {
                                add(aspectList.first())
                                add(aspectList[3])
                                add(aspectList[4])
                            }
                        }
                        VerticalImageMediaFrameLayout(
                            modifier = Modifier,
                            containerWidth = mainlyWidth!!,
                            style = style,
                            topAspect = firstAspect,
                            fixedHeight = true,
                            bottomAspect = verticalList.averageDropFirst(1).toFloat(),
                            topContent = {
                                itemContent(0)
                            },
                            bottomContent = {
                                HorizontalImageMediaListLayout(
                                    modifier = Modifier.fillMaxSize(),
                                    style = style,
                                    dropFirst = 1,
                                    aspectList = verticalList,
                                    itemContent = { index ->
                                        itemContent(2 + index)
                                    }
                                )
                            },
                        )
                    }
                }
            },
            endContent = {
                VerticalImageMediaListLayout(
                    modifier = Modifier.fillMaxSize(),
                    style = style,
                    dropFirst = 1,
                    aspectList = aspectList.take(3),
                    itemContent = itemContent,
                )
            },
        )
    } else {
        val bottomAspectList = aspectList.drop(3)
        VerticalImageMediaFrameLayout(
            modifier = modifier,
            containerWidth = containerWidth,
            style = style,
            topAspect = aspectList.first(),
            bottomAspect = bottomAspectList.average().toFloat(),
            topContent = {
                var mainlyWidth: Dp? by remember {
                    mutableStateOf(null)
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .onGloballyPositioned {
                            mainlyWidth = it.size.width.pxToDp(density)
                        }
                ) {
                    if (mainlyWidth != null) {
                        HorizontalImageMediaFrameLayout(
                            modifier = Modifier,
                            containerWidth = mainlyWidth!!,
                            style = style,
                            fixedHeight = true,
                            startAspect = aspectList.first(),
                            startContent = {
                                itemContent(0)
                            },
                            endContent = {
                                VerticalImageMediaListLayout(
                                    modifier = Modifier.fillMaxSize(),
                                    style = style,
                                    dropFirst = 0,
                                    aspectList = listOf(aspectList.second(), aspectList.third()),
                                    itemContent = { index ->
                                        itemContent(1 + index)
                                    }
                                )
                            }
                        )
                    }
                }
            },
            bottomContent = {
                HorizontalImageMediaListLayout(
                    modifier = Modifier.fillMaxSize(),
                    style = style,
                    dropFirst = 0,
                    aspectList = bottomAspectList,
                    itemContent = { index ->
                        itemContent(3 + index)
                    }
                )
            },
        )
    }
}
