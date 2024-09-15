package com.zhangke.fread.feeds.pages.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import com.zhangke.framework.composable.Toolbar
import com.zhangke.fread.common.page.BaseScreen
import com.zhangke.fread.feeds.R
import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.ui.BlogAuthorAvatar

class SelectAccountForPostStatusScreen(
    private val accountList: List<LoggedAccount>,
    private val onAccountSelected: (LoggedAccount) -> Unit,
) : BaseScreen() {

    companion object {

        const val SCREEN_KEY =
            "com.zhangke.fread.feeds.pages.home.SelectAccountForPostStatusScreen"
    }

    override val key: ScreenKey
        get() = SCREEN_KEY

    @Composable
    override fun Content() {
        super.Content()
        val bottomSheetNavigator = LocalBottomSheetNavigator.current
        Column(modifier = Modifier.fillMaxWidth()) {
            Toolbar(
                title = stringResource(R.string.feeds_select_account_for_post_status),
            )

            accountList.forEach { account ->
                LoggedAccountUi(
                    modifier = Modifier.fillMaxWidth(),
                    account = account,
                    onClick = {
                        onAccountSelected(it)
                        bottomSheetNavigator.hide()
                    },
                )
                Box(modifier = Modifier.height(16.dp))
            }
            Box(modifier = Modifier.height(10.dp))
        }
    }

    @Composable
    private fun LoggedAccountUi(
        modifier: Modifier,
        account: LoggedAccount,
        onClick: (LoggedAccount) -> Unit,
    ) {
        Card(
            modifier = modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
        ) {
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClick(account) }
                    .padding(vertical = 8.dp)
            ) {
                val (avatarRef, nameRef, acctRef) = createRefs()
                BlogAuthorAvatar(
                    modifier = Modifier
                        .size(48.dp)
                        .constrainAs(avatarRef) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start, 16.dp)
                        },
                    imageUrl = account.avatar,
                )
                Text(
                    modifier = Modifier
                        .constrainAs(nameRef) {
                            top.linkTo(avatarRef.top)
                            end.linkTo(parent.end, 16.dp)
                            start.linkTo(avatarRef.end, 16.dp)
                            width = Dimension.fillToConstraints
                        },
                    textAlign = TextAlign.Left,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    text = account.userName,
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    modifier = Modifier
                        .constrainAs(acctRef) {
                            top.linkTo(nameRef.bottom, 2.dp)
                            start.linkTo(nameRef.start)
                            end.linkTo(parent.end, 16.dp)
                            width = Dimension.fillToConstraints
                        },
                    textAlign = TextAlign.Left,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    text = account.webFinger.toString(),
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}
