package com.zhangke.utopia.feeds.pages.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import com.zhangke.framework.composable.Toolbar
import com.zhangke.utopia.feeds.R
import com.zhangke.utopia.status.account.LoggedAccount

class SelectAccountForPostStatusScreen(
    private val accountList: List<LoggedAccount>,
) : Screen {

    @Composable
    override fun Content() {
        Column(modifier = Modifier.fillMaxWidth()) {
            Toolbar(
                title = stringResource(R.string.feeds_select_account_for_post_status),
            )

            accountList.forEach { account ->
                Card(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    TextButton(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        onClick = {},
                    ) {
                        Text(text = stringResource(R.string.login_dialog_input_tip))
                    }
                }
            }
        }
    }
}