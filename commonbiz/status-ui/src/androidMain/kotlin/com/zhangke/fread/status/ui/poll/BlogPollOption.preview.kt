package com.zhangke.fread.status.ui.poll

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Preview(backgroundColor = 0xffffffff)
@Composable
private fun PreviewMoreLineBlogPollOption() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
            .padding(horizontal = 15.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        BlogPollOption(
            modifier = Modifier
                .fillMaxWidth(),
            optionContent = "12344",
            selected = true,
            votable = true,
            showProgress = true,
            progress = 0F,
            onClick = {},
        )
        BlogPollOption(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            optionContent = "12344",
            selected = true,
            votable = true,
            showProgress = true,
            progress = 0F,
            onClick = {},
        )
        BlogPollOption(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            optionContent = "12344",
            selected = true,
            votable = true,
            progress = 0F,
            showProgress = true,
            onClick = {},
        )
        BlogPollOption(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            optionContent = "123441234412344123441234412344123441234412344123441234412344123441234412344123441234412344123441234412344123441234412344123441234412344",
            progress = 0.5F,
            selected = true,
            votable = true,
            showProgress = true,
            onClick = {},
        )
    }
}
