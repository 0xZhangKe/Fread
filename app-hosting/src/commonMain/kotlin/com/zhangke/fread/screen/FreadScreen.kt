package com.zhangke.fread.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zhangke.framework.utils.WebFinger
import com.zhangke.fread.activitypub.app.createActivityPubProtocol
import com.zhangke.fread.common.page.BaseScreen
import com.zhangke.fread.status.model.Mention
import com.zhangke.fread.status.richtext.parser.HtmlParser
import kotlinx.coroutines.runBlocking

class FreadScreen : BaseScreen() {

    @Composable
    override fun Content() {
        super.Content()
//        MainPage()

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            var text by remember { mutableStateOf("") }

            androidx.compose.material3.Text(text)
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = {
                    val html = """
                        <p><span class="h-card" translate="no"><a href="https://mastodon.social/@fread" class="u-url mention">@<span>fread</span></a></span> sss</p>
                    """.trimIndent()
                    val mention = Mention(
                        id = "123",
                        username = "fread",
                        url = "https://mastodon.social/@fread",
                        webFinger = WebFinger.create("@fread@mastodon.social")!!,
                        protocol = runBlocking { createActivityPubProtocol() },
                    )
                    text = HtmlParser.parseToPlainText(html, listOf(mention))
                },
            ) {
                Text("Parse")
            }
        }
    }
}
