package com.zhangke.fread.status.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.zhangke.framework.composable.freadPlaceholder
import com.zhangke.fread.status.ui.style.StatusInfoStyleDefaults

@Composable
fun StatusPlaceHolder(
    modifier: Modifier = Modifier,
) {
    ConstraintLayout(modifier = modifier) {
        val (avatarRef, nameRef, descRef, contentRef) = createRefs()
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .constrainAs(avatarRef) {
                    top.linkTo(parent.top, margin = 8.dp)
                    start.linkTo(parent.start, 16.dp)
                    width = Dimension.value(StatusInfoStyleDefaults.avatarSize)
                    height = Dimension.value(StatusInfoStyleDefaults.avatarSize)
                }
                .freadPlaceholder(true),
        )
        Box(
            modifier = Modifier
                .height(16.dp)
                .constrainAs(nameRef) {
                    top.linkTo(parent.top, 4.dp)
                    start.linkTo(avatarRef.end, StatusInfoStyleDefaults.avatarToNamePadding)
                    width = Dimension.value(100.dp)
                }
                .freadPlaceholder(true),
        )
        Box(
            modifier = Modifier
                .height(12.dp)
                .constrainAs(descRef) {
                    top.linkTo(nameRef.bottom, StatusInfoStyleDefaults.nameToTimePadding)
                    start.linkTo(nameRef.start)
                    width = Dimension.value(200.dp)
                }
                .freadPlaceholder(true),
        )
        Column(
            modifier = Modifier.constrainAs(contentRef) {
                top.linkTo(descRef.bottom, 4.dp)
                start.linkTo(nameRef.start)
                end.linkTo(parent.end, 16.dp)
                bottom.linkTo(parent.bottom, 8.dp)
                width = Dimension.fillToConstraints
                height = Dimension.wrapContent
            },
        ) {
            repeat(3) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                        .height(14.dp)
                        .freadPlaceholder(true),
                )
            }
        }
    }
}

@Composable
fun StatusListPlaceholder(
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            repeat(8) {
                StatusPlaceHolder(modifier = Modifier.fillMaxWidth())
                Box(modifier = Modifier.height(6.dp))
            }
        }
    }
}
