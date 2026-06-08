package com.zhangke.fread.commonbiz.shared.screen.publish

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zhangke.framework.utils.getDefaultLocale
import com.zhangke.framework.utils.getDisplayName
import com.zhangke.framework.utils.initLocale
import com.zhangke.fread.localization.LocalizedString
import org.jetbrains.compose.resources.stringResource

@Composable
fun SuggestedLanguageBanner(
    modifier: Modifier,
    languageTag: String,
    onAccept: () -> Unit,
    onDismiss: () -> Unit,
) {
    val displayLocale = remember { getDefaultLocale() }
    val displayName = remember(languageTag, displayLocale) {
        initLocale(languageTag).getDisplayName(displayLocale).ifBlank { languageTag }
    }
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Row(
            modifier = Modifier.padding(start = 12.dp, end = 4.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                imageVector = Icons.Default.Language,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                modifier = Modifier.weight(1F),
                text = stringResource(LocalizedString.sharedPublishSuggestedLanguage, displayName),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            TextButton(onClick = onAccept) {
                Text(text = stringResource(LocalizedString.ok))
            }
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(LocalizedString.cancel),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
