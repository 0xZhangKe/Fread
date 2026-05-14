package com.zhangke.fread.status.ui.user

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * One row in [UserAboutCard] — a labelled key/value pair (e.g. "Joined / 2024-01-01").
 */
data class UserAboutField(
    val key: String,
    val value: String,
)

/**
 * Generic "About this user" card used on profile detail screens. Mastodon's
 * existing UserAboutCard predates this and renders Mastodon's user-defined
 * profile fields with emoji/rich-text support; this shared version handles the
 * simpler case of plain-text rows, suitable for things like Bluesky's
 * moderation labels.
 */
@Composable
fun UserAboutCard(
    fields: List<UserAboutField>,
    modifier: Modifier = Modifier,
) {
    if (fields.isEmpty()) return
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme
                .surfaceContainerHighest
                .copy(alpha = 0.3F),
        ),
    ) {
        SelectionContainer {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
                fields.forEach { field ->
                    UserAboutFieldRow(field)
                }
            }
        }
    }
}

@Composable
private fun UserAboutFieldRow(field: UserAboutField) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier.weight(2F),
            text = field.key,
            maxLines = 1,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.labelMedium,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Box(
            modifier = Modifier.weight(4F),
            contentAlignment = Alignment.CenterEnd,
        ) {
            Text(
                text = field.value,
                textAlign = TextAlign.End,
                maxLines = 3,
                fontSize = 14.sp,
            )
        }
    }
}
