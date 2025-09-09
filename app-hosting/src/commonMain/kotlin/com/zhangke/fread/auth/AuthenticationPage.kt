package com.zhangke.fread.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zhangke.fread.commonbiz.login
import com.zhangke.fread.localization.Res
import com.zhangke.fread.localization.cancel
import com.zhangke.fread.hosting.authentication_page_failed_title
import com.zhangke.fread.hosting.authentication_page_normal_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun AuthenticationPage(
    isOauthFailed: Boolean,
    onCancelClick: () -> Unit,
    onLoginClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xff424242))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
            ) {
                onCancelClick()
            }
    ) {

        Card(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(start = 50.dp, end = 50.dp)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                ) {}
        ) {

            Column {

                val loginMessageResId = if (isOauthFailed) {
                    com.zhangke.fread.hosting.Res.string.authentication_page_failed_title
                } else {
                    com.zhangke.fread.hosting.Res.string.authentication_page_normal_title
                }

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 50.dp, top = 20.dp, end = 50.dp),
                    text = stringResource(loginMessageResId),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 15.dp, top = 15.dp, end = 15.dp, bottom = 15.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Button(
                        onClick = onCancelClick
                    ) {
                        Text(text = stringResource(Res.string.cancel))
                    }
                    Button(
                        modifier = Modifier.padding(start = 15.dp),
                        onClick = onLoginClick
                    ) {
                        Text(text = stringResource(com.zhangke.fread.commonbiz.Res.string.login))
                    }
                }
            }
        }
    }
}
