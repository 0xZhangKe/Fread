package com.zhangke.fread.activitypub.app.internal.screen.status.post

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zhangke.framework.composable.noRippleClick
import com.zhangke.fread.commonbiz.shared.screen.publish.PublishSettingLabel
import com.zhangke.fread.commonbiz.shared.screen.publish.composable.InteractionOption
import com.zhangke.fread.commonbiz.shared.screen.publish.multi.describeStringId
import com.zhangke.fread.localization.LocalizedString
import com.zhangke.fread.status.model.QuoteApprovalPolicy
import com.zhangke.fread.status.model.StatusVisibility
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublishInteractionSettingLabel(
    modifier: Modifier,
    visibility: StatusVisibility,
    visibilityChangeable: Boolean,
    quoteApprovalPolicy: QuoteApprovalPolicy,
    quoteApprovalPolicyChangeable: Boolean,
    onVisibilitySelect: (StatusVisibility) -> Unit,
    onQuoteApprovalPolicySelect: (QuoteApprovalPolicy) -> Unit,
) {
    var sheetVisibility by remember { mutableStateOf(false) }
    val noLimit =
        visibility == StatusVisibility.PUBLIC && quoteApprovalPolicy == QuoteApprovalPolicy.PUBLIC
    PublishSettingLabel(
        modifier = modifier.noRippleClick(
            enabled = visibilityChangeable || quoteApprovalPolicyChangeable,
        ) {
            sheetVisibility = true
        },
        label = if (noLimit) {
            stringResource(LocalizedString.sharedPublishInteractionNoLimit)
        } else {
            stringResource(LocalizedString.sharedPublishInteractionLimited)
        },
        icon = if (noLimit) Icons.Default.Public else Icons.Outlined.Group,
    )
    val coroutineScope = rememberCoroutineScope()
    val state = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    if (sheetVisibility) {
        ModalBottomSheet(
            onDismissRequest = {
                coroutineScope.launch {
                    state.hide()
                    sheetVisibility = false
                }
            },
            sheetState = state,
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
                    .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 32.dp)
                    .verticalScroll(rememberScrollState()),
            ) {
                Text(
                    modifier = Modifier.padding(top = 26.dp),
                    text = stringResource(LocalizedString.sharedPublishInteractionDialogQuoteTitle),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                    ),
                )

                InteractionOption(
                    modifier = Modifier.padding(top = 16.dp).fillMaxWidth()
                        .noRippleClick(enabled = quoteApprovalPolicyChangeable) {
                            onQuoteApprovalPolicySelect(QuoteApprovalPolicy.PUBLIC)
                        },
                    text = QuoteApprovalPolicy.PUBLIC.label,
                    selected = quoteApprovalPolicy == QuoteApprovalPolicy.PUBLIC,
                )

                InteractionOption(
                    modifier = Modifier.padding(top = 16.dp).fillMaxWidth()
                        .noRippleClick(enabled = quoteApprovalPolicyChangeable) {
                            onQuoteApprovalPolicySelect(QuoteApprovalPolicy.FOLLOWERS)
                        },
                    text = QuoteApprovalPolicy.FOLLOWERS.label,
                    selected = quoteApprovalPolicy == QuoteApprovalPolicy.FOLLOWERS,
                )

                InteractionOption(
                    modifier = Modifier.padding(top = 16.dp).fillMaxWidth()
                        .noRippleClick(enabled = quoteApprovalPolicyChangeable) {
                            onQuoteApprovalPolicySelect(QuoteApprovalPolicy.NOBODY)
                        },
                    text = QuoteApprovalPolicy.NOBODY.label,
                    selected = quoteApprovalPolicy == QuoteApprovalPolicy.NOBODY,
                )


                Text(
                    modifier = Modifier.padding(top = 26.dp),
                    text = stringResource(LocalizedString.status_ui_visibility),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                    ),
                )

                InteractionOption(
                    modifier = Modifier.padding(top = 16.dp).fillMaxWidth()
                        .noRippleClick(enabled = quoteApprovalPolicyChangeable) {
                            onVisibilitySelect(StatusVisibility.PUBLIC)
                        },
                    text = stringResource(StatusVisibility.PUBLIC.describeStringId),
                    selected = visibility == StatusVisibility.PUBLIC,
                )

                InteractionOption(
                    modifier = Modifier.padding(top = 16.dp).fillMaxWidth()
                        .noRippleClick(enabled = quoteApprovalPolicyChangeable) {
                            onVisibilitySelect(StatusVisibility.UNLISTED)
                        },
                    text = stringResource(StatusVisibility.UNLISTED.describeStringId),
                    selected = visibility == StatusVisibility.UNLISTED,
                )

                InteractionOption(
                    modifier = Modifier.padding(top = 16.dp).fillMaxWidth()
                        .noRippleClick(enabled = quoteApprovalPolicyChangeable) {
                            onVisibilitySelect(StatusVisibility.PRIVATE)
                        },
                    text = stringResource(StatusVisibility.PRIVATE.describeStringId),
                    selected = visibility == StatusVisibility.PRIVATE,
                )

                InteractionOption(
                    modifier = Modifier.padding(top = 16.dp).fillMaxWidth()
                        .noRippleClick(enabled = quoteApprovalPolicyChangeable) {
                            onVisibilitySelect(StatusVisibility.DIRECT)
                        },
                    text = stringResource(StatusVisibility.DIRECT.describeStringId),
                    selected = visibility == StatusVisibility.DIRECT,
                )
            }
        }
    }
}

private val QuoteApprovalPolicy.label: String
    @Composable
    get() = when (this) {
        QuoteApprovalPolicy.PUBLIC -> stringResource(LocalizedString.status_ui_quote_approval_public)
        QuoteApprovalPolicy.FOLLOWERS -> stringResource(LocalizedString.status_ui_quote_approval_follower)
        QuoteApprovalPolicy.NOBODY -> stringResource(LocalizedString.status_ui_quote_approval_nobody)
        QuoteApprovalPolicy.FOLLOWING -> "Following"
    }
