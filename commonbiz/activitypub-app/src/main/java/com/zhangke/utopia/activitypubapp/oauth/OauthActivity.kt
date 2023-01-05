package com.zhangke.utopia.activitypubapp.oauth

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
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
import com.zhangke.framework.architect.theme.UtopiaTheme
import com.zhangke.utopia.activitypubapp.R

/**
 * Created by ZhangKe on 2022/12/14.
 */
class OauthActivity : AppCompatActivity() {

    companion object {

        private const val ARG_OAUTH_FAILED = "arg_login_failed"

        fun open(activity: AppCompatActivity, isOauthFailed: Boolean = false) {
            activity.startActivity(Intent(activity, OauthActivity::class.java).apply {
                putExtra(ARG_OAUTH_FAILED, isOauthFailed)
                addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val isOauthFailed = intent.getBooleanExtra(ARG_OAUTH_FAILED, false)

        setContent {
            UtopiaTheme {
                OauthPage(
                    isOauthFailed = isOauthFailed,
                    onCancelClick = {
                        finish()
                    },
                    onLoginClick = {
                        finish()
                    }
                )
            }
        }
    }

    override fun onPause() {
        super.onPause()
        overridePendingTransition(0, 0) // disable activity finish animation
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun OauthPage(
        isOauthFailed: Boolean,
        onCancelClick: () -> Unit,
        onLoginClick: () -> Unit
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

                    val loginMessageResId =
                        if (isOauthFailed) R.string.login_page_auth_failed_title else R.string.login_page_title

                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 50.dp, top = 20.dp, end = 50.dp),
                        text = getString(loginMessageResId),
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
                            Text(text = getString(R.string.cancel))
                        }
                        Button(
                            modifier = Modifier.padding(start = 15.dp),
                            onClick = onLoginClick
                        ) {
                            Text(text = getString(R.string.login))
                        }
                    }
                }
            }
        }
    }
}