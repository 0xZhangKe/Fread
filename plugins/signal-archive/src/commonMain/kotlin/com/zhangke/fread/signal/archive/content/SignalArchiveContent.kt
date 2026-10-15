package com.zhangke.fread.signal.archive.content

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.zhangke.framework.architect.json.JsonModuleBuilder
import com.zhangke.fread.localization.LocalizedString
import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.model.FreadContent
import com.zhangke.fread.status.uri.FormalUri
import com.zhangke.krouter.annotation.Service
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModuleBuilder
import kotlinx.serialization.serializer
import org.jetbrains.compose.resources.stringResource

@Service
class SignalArchiveContentJsonModuleBuilder : JsonModuleBuilder {

    override fun SerializersModuleBuilder.buildSerializersModule() {
        polymorphic(
            baseClass = FreadContent::class,
            actualClass = SignalArchiveContent::class,
            actualSerializer = serializer(),
        )
    }
}

@Serializable
data class SignalArchiveContent(
    override val id: String,
    override val order: Int,
    override val name: String,
    val baseUrl: String = "",
): FreadContent {

    override val accountUri: FormalUri? = null

    override fun newOrder(newOrder: Int): FreadContent {
        return copy(order = newOrder)
    }

    @Composable
    override fun Subtitle(account: LoggedAccount?) {
        Text(
            modifier = Modifier,
            text = stringResource(LocalizedString.signalArchiveContentName),
            style = MaterialTheme.typography.labelMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
