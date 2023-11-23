package com.zhangke.utopia.activitypub.app.internal.screen.server.about

import android.widget.TextView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
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
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import com.zhangke.activitypub.entities.ActivityPubAnnouncementEntity
import com.zhangke.utopia.activitypub.app.R
import com.zhangke.utopia.activitypub.app.internal.model.ActivityPubInstanceRule

@Composable
internal fun Screen.ServerAboutPage(
    host: String,
    rules: List<ActivityPubInstanceRule> = emptyList(),
    contentCanScrollBackward: MutableState<Boolean>,
) {
    val viewModel: ServerAboutViewModel = getViewModel()
    viewModel.rules = rules
    viewModel.host = host
    LaunchedEffect(viewModel) {
        viewModel.onPageResume()
    }
    val uiState by viewModel.uiState.collectAsState()
    ServerAboutPageContent(
        uiState = uiState,
        contentCanScrollBackward = contentCanScrollBackward,
    )
}

@Composable
private fun ServerAboutPageContent(
    uiState: ServerAboutUiState,
    contentCanScrollBackward: MutableState<Boolean>,
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
                    .padding(start = 10.dp, top = 15.dp, end = 10.dp, bottom = 10.dp),
                announcementList = uiState.announcement,
            )
        }
        if (uiState.rules.isNotEmpty()) {
            ServerAboutRulesSection(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, top = 5.dp, end = 10.dp, bottom = 10.dp),
                ruleList = uiState.rules,
            )
        }
    }
}

@Composable
private fun ServerAboutAnnouncementSection(
    modifier: Modifier = Modifier,
    announcementList: List<ActivityPubAnnouncementEntity>,
) {
    Column(modifier = modifier) {
        announcementList.forEach {
            ServerAboutAnnouncement(
                modifier = Modifier.fillMaxWidth(),
                entity = it,
            )
        }
    }
}

@Composable
private fun ServerAboutAnnouncement(
    modifier: Modifier = Modifier,
    entity: ActivityPubAnnouncementEntity,
) {
    AndroidView(
        modifier = modifier,
        factory = { TextView(it) },
        update = {
            it.text = HtmlCompat.fromHtml(entity.content, HtmlCompat.FROM_HTML_MODE_COMPACT)
        }
    )
}

@Composable
private fun ServerAboutRulesSection(
    modifier: Modifier = Modifier,
    ruleList: List<ActivityPubInstanceRule>,
) {
    Text(
        modifier = modifier,
        text = stringResource(R.string.activity_pub_about_rule_title),
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        color = Color.Black,
    )
    ruleList.forEachIndexed { index, rule ->
        ServerAboutRule(
            modifier = Modifier.padding(start = 15.dp, end = 15.dp),
            rule = rule,
            showDivider = index != ruleList.lastIndex,
        )
    }
}

@Composable
private fun ServerAboutRule(
    modifier: Modifier = Modifier,
    rule: ActivityPubInstanceRule,
    showDivider: Boolean = true,
) {
    Box(
        modifier = modifier
            .padding(top = 5.dp)
    ) {
        Row(modifier = Modifier.padding(end = 2.dp, bottom = 10.dp)) {
            Text(
                text = "${rule.id}.",
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
        if (showDivider) {
            Divider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 1.dp,
            )
        }
    }
}
