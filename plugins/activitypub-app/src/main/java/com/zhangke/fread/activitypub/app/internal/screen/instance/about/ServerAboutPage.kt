package com.zhangke.fread.activitypub.app.internal.screen.instance.about

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import com.zhangke.activitypub.entities.ActivityPubAnnouncementEntity
import com.zhangke.activitypub.entities.ActivityPubInstanceEntity
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.fread.activitypub.app.R
import com.zhangke.fread.common.browser.LocalActivityBrowserLauncher
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.ui.richtext.FreadRichText

@Composable
internal fun Screen.ServerAboutPage(
    baseUrl: FormalBaseUrl,
    rules: List<ActivityPubInstanceEntity.Rule> = emptyList(),
    contentCanScrollBackward: MutableState<Boolean>,
) {
    val viewModel: ServerAboutViewModel = getViewModel()
    LaunchedEffect(viewModel) {
        viewModel.rules = rules
        viewModel.baseUrl = baseUrl
        viewModel.onPageResume()
    }
    val uiState by viewModel.uiState.collectAsState()
    ServerAboutPageContent(
        uiState = uiState,
        contentCanScrollBackward = contentCanScrollBackward,
        baseUrl = baseUrl,
    )
}

@Composable
private fun ServerAboutPageContent(
    uiState: ServerAboutUiState,
    contentCanScrollBackward: MutableState<Boolean>,
    baseUrl: FormalBaseUrl,
) {
    val scrollState = rememberScrollState()
    contentCanScrollBackward.value = scrollState.value > 0
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
    ) {
        if (uiState.announcement.isNotEmpty()) {
            ServerAboutAnnouncementSection(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 8.dp),
                announcementList = uiState.announcement,
                baseUrl = baseUrl,
            )
        }
        if (uiState.rules.isNotEmpty()) {
            ServerAboutRulesSection(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 6.dp, end = 16.dp, bottom = 36.dp),
                ruleList = uiState.rules,
            )
        }
    }
}

@Composable
private fun ServerAboutAnnouncementSection(
    modifier: Modifier = Modifier,
    announcementList: List<ActivityPubAnnouncementEntity>,
    baseUrl: FormalBaseUrl,
) {
    Column(modifier = modifier) {
        announcementList.forEach {
            ServerAboutAnnouncement(
                modifier = Modifier.fillMaxWidth(),
                entity = it,
                baseUrl = baseUrl,
            )
        }
    }
}

@Composable
private fun ServerAboutAnnouncement(
    modifier: Modifier = Modifier,
    entity: ActivityPubAnnouncementEntity,
    baseUrl: FormalBaseUrl,
) {
    val browserLauncher = LocalActivityBrowserLauncher.current
    FreadRichText(
        modifier = modifier,
        content = entity.content,
        textSelectable = true,
        onUrlClick = {
            val role = IdentityRole(null, baseUrl = baseUrl)
            browserLauncher.launchWebTabInApp(it, role)
        },
    )
}

@Composable
private fun ServerAboutRulesSection(
    modifier: Modifier = Modifier,
    ruleList: List<ActivityPubInstanceEntity.Rule>,
) {
    SelectionContainer {
        Column(modifier = modifier) {
            Text(
                modifier = Modifier,
                text = stringResource(R.string.activity_pub_about_rule_title),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge,
            )
            Spacer(modifier = Modifier.height(4.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(4.dp))
            ruleList.forEachIndexed { index, rule ->
                ServerAboutRule(
                    modifier = Modifier,
                    rule = rule,
                    index = index,
                )
                Spacer(modifier = Modifier.height(4.dp))
                if (index != ruleList.lastIndex) {
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun ServerAboutRule(
    modifier: Modifier = Modifier,
    rule: ActivityPubInstanceEntity.Rule,
    index: Int,
) {
    Row(modifier = modifier) {
        Text(
            text = "${index + 1}.",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
        )

        Text(
            modifier = Modifier.padding(start = 6.dp),
            text = rule.text,
            fontSize = 14.sp,
        )
    }
}
