package com.zhangke.utopia.pages

import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.widget.TextView
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import cafe.adriel.voyager.core.screen.Screen
import com.zhangke.framework.composable.textOf
import com.zhangke.utopia.pages.main.MainPage
import com.zhangke.utopia.status.model.Emoji
import com.zhangke.utopia.status.model.Hashtag
import com.zhangke.utopia.status.model.StatusProviderProtocol
import com.zhangke.utopia.status.richtext.android.HtmlParser
import com.zhangke.utopia.status.richtext.android.span.CustomEmojiSpan
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class UtopiaScreen : Screen {

    private val richText1 = """
        <p><a href="https://m.cmx.im/tags/%E6%BC%82%E4%BA%AE%E7%9A%84%E5%B0%8F%E7%8E%A9%E6%84%8F" class="mention hashtag" rel="tag">#<span>漂亮的小玩意</span></a><br />前几天网上看到个很复古的老式面包机，完全长在我审美上了，搜了半天找到个专门卖复古面包机的网站，但貌似只能发货到美国 :awesome: ，但真的好喜欢这个小玩意。<br /><a href="https://www.toastercentral.com/index.htm" target="_blank" rel="nofollow noopener noreferrer" translate="no"><span class="invisible">https://www.</span><span class="">toastercentral.com/index.htm</span><span class="invisible"></span></a></p>
    """.trimIndent()

    //
    private val richText2 = """
        <p>RichText 测试 <span class="h-card" translate="no"><a href="https://androiddev.social/@webb" class="u-url mention">@<span>webb</span></a></span> :awesome_rotate: 🔖 ，<a href="https://m.cmx.im/tags/facebook" class="mention hashtag" rel="tag">#<span>facebook</span></a> <br /> 测试结束。</p>
    """.trimIndent()

    private val richText3 = """
        <p>可爱小猫来吃鱼罐头啦 姿势透着戒备...</p>
    """.trimIndent()

//    @Composable
//    private fun RichTextPreview() {
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(horizontal = 16.dp)
//        ) {
//            Box(modifier = Modifier.height(80.dp))
//            RichText(
//                modifier = Modifier.fillMaxWidth(),
//                document = richText1,
//                host = "https://m.cmx.im",
//                emojis = listOf(
//                    Emoji(
//                        shortcode = "awesome",
//                        url = "https://media.cmx.edu.kg/custom_emojis/images/000/067/590/original/72ae4469639d0a2e.png",
//                        staticUrl = "https://media.cmx.edu.kg/custom_emojis/images/000/067/590/static/72ae4469639d0a2e.png",
//                    )
//                ),
//                mentions = emptyList(),
//                fontSp = 14F,
//            )
//
//            val coroutineScope = rememberCoroutineScope()
//            AndroidView(
//                factory = {
//                    val view = View(it)
//                    view.layoutParams = ViewGroup.LayoutParams(200, 200)
//                    view.background = AsyncImageDrawable(
//                        view = view,
//                        url = "https://media.cmx.edu.kg/custom_emojis/images/000/067/590/original/72ae4469639d0a2e.png",
//                        context = it,
//                        coroutineScope = coroutineScope,
//                    )
////                    view.background = TestDrawable()
//                    view
//                },
//            )
//
//            Box(modifier = Modifier.height(16.dp))
//            HorizontalDivider()
//            Box(modifier = Modifier.height(16.dp))
//
//            RichText(
//                modifier = Modifier.fillMaxWidth(),
//                text = richText2,
////                host = "https://m.cmx.im",
//                emojis = listOf(
//                    Emoji(
//                        shortcode = "awesome_rotate",
//                        url = "https://media.cmx.edu.kg/custom_emojis/images/000/067/591/original/a5b37107a75ab054.png",
//                        staticUrl = "https://media.cmx.edu.kg/custom_emojis/images/000/067/591/static/a5b37107a75ab054.png",
//                    )
//                ),
////                mentions = listOf(
////                    Mention(
////                        id = "111199778856627994",
////                        username = "webb",
////                        url = "https://androiddev.social/@webb",
////                        webFinger = WebFinger.create("webb@androiddev.social")!!
////                    )
////                ),
//                fontSp = 14F,
//            )
//
//            Box(modifier = Modifier.height(16.dp))
//            HorizontalDivider()
//            Box(modifier = Modifier.height(16.dp))
//
//
//            RichText(
//                modifier = Modifier.fillMaxWidth(),
//                text = richText3,
////                host = "https://m.cmx.im",
//                emojis = emptyList(),
////                mentions = emptyList(),
//                fontSp = 14F,
//            )
//        }
//    }

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
    @Composable
    override fun Content() {
        MainPage()

//        RichTextPreview()

//        AndroidRichTextPreview()
    }

    @Composable
    private fun AndroidRichTextPreview() {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(100.dp))

            val coroutineScope = rememberCoroutineScope()
            AndroidView(
                factory = {
                    val view = TextView(it)
                    view.movementMethod = LinkMovementMethod.getInstance()
                    view.textSize = 14F
                    val spans = buildSpan(coroutineScope, view)
                    view.text = spans
                    val customEmojiSpans =
                        spans.getSpans(0, spans.length, CustomEmojiSpan::class.java)
                    coroutineScope.launch {
                        customEmojiSpans.map { emojiSpan ->
                            async {
                                if (emojiSpan.loadDrawable(it)) {
                                    view.invalidate()
                                }
                            }
                        }.awaitAll()
                    }
                    view
                },
            )
        }
    }

    private fun buildSpan(
        coroutineScope: CoroutineScope,
        textView: TextView,
    ): SpannableStringBuilder {
        return HtmlParser.parse(
            document = richText1,
            hashTag = listOf(
                Hashtag(
                    name = "漂亮的小玩意",
                    url = "https://m.cmx.im/tags/%E6%BC%82%E4%BA%AE%E7%9A%84%E5%B0%8F%E7%8E%A9%E6%84%8F",
                    description = textOf("漂亮的小玩意"),
                    history = Hashtag.History(
                        history = emptyList(),
                        min = null,
                        max = null,
                    ),
                    following = false,
                    protocol = StatusProviderProtocol("", ""),
                )
            ),
            mentions = emptyList(),
            emojis = listOf(
                Emoji(
                    shortcode = "awesome",
                    url = "https://media.cmx.edu.kg/custom_emojis/images/000/067/590/original/72ae4469639d0a2e.png",
                    staticUrl = "https://media.cmx.edu.kg/custom_emojis/images/000/067/590/static/72ae4469639d0a2e.png",
                )
            ),
        )
    }
}
