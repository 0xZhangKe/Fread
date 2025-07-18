package com.zhangke.fread.activitypub.app.internal.screen.user.about

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import com.zhangke.activitypub.entities.ActivityPubField
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.LocalSnackbarHostState
import com.zhangke.framework.composable.PagerTabOptions
import com.zhangke.framework.composable.applyNestedScrollConnection
import com.zhangke.framework.utils.WebFinger
import com.zhangke.fread.activitypub.app.Res
import com.zhangke.fread.activitypub.app.activity_pub_user_detail_tab_about
import com.zhangke.fread.activitypub.app.activity_pub_user_detail_tab_about_joined
import com.zhangke.fread.common.browser.LocalActivityBrowserLauncher
import com.zhangke.fread.common.page.BasePagerTab
import com.zhangke.fread.status.model.Emoji
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.ui.richtext.FreadRichText
import org.jetbrains.compose.resources.stringResource

internal class UserAboutTab(
    private val contentCanScrollBackward: MutableState<Boolean>,
    private val locator: PlatformLocator,
    private val userWebFinger: WebFinger,
    private val userId: String?,
) : BasePagerTab() {

    override val options: PagerTabOptions
        @Composable get() = PagerTabOptions(
            title = stringResource(Res.string.activity_pub_user_detail_tab_about)
        )

    @Composable
    override fun TabContent(screen: Screen, nestedScrollConnection: NestedScrollConnection?) {
        super.TabContent(screen, nestedScrollConnection)
        val viewModel = screen.getViewModel<UserAboutContainerViewModel>()
            .getViewModel(locator, userWebFinger, userId)
        val uiState by viewModel.uiState.collectAsState()
        UserAboutContent(
            uiState = uiState,
            nestedScrollConnection = nestedScrollConnection,
        )
        val snackBarState = LocalSnackbarHostState.current
        if (snackBarState != null) {
            ConsumeSnackbarFlow(snackBarState, viewModel.messageFlow)
        }
    }

    @Composable
    private fun UserAboutContent(
        uiState: UserAboutUiState,
        nestedScrollConnection: NestedScrollConnection?,
    ) {
        val scrollState = rememberScrollState()
        contentCanScrollBackward.value = scrollState.value > 0
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .applyNestedScrollConnection(nestedScrollConnection)
                .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 100.dp)
        ) {
            if (!uiState.joinedDatetime.isNullOrEmpty()) {
                Text(
                    text = stringResource(
                        Res.string.activity_pub_user_detail_tab_about_joined,
                        uiState.joinedDatetime
                    ),
                )
            }

            for (field in uiState.fieldList) {
                FieldUi(
                    field = field,
                    emojis = uiState.emojis,
                )
            }
        }
    }

    @Composable
    private fun FieldUi(
        field: ActivityPubField,
        emojis: List<Emoji>,
    ) {
        val browserLauncher = LocalActivityBrowserLauncher.current
        Text(
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth(),
            text = field.name,
        )
        FreadRichText(
            modifier = Modifier
                .padding(top = 4.dp)
                .fillMaxWidth(),
            content = field.value,
            mentions = emptyList(),
            tags = emptyList(),
            onMentionClick = {},
            onHashtagClick = {},
            emojis = emojis,
            onUrlClick = {
                browserLauncher.launchWebTabInApp(it, locator)
            },
        )
    }
}
