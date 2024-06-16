package com.zhangke.fread.screen

import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.widget.TextView
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import cafe.adriel.voyager.core.screen.Screen
import com.zhangke.framework.loadable.lazycolumn.LoadMoreUi
import com.zhangke.framework.utils.LoadState
import com.zhangke.fread.screen.main.MainPage
import com.zhangke.fread.status.model.Emoji
import com.zhangke.fread.status.model.HashtagInStatus
import com.zhangke.fread.status.model.StatusProviderProtocol
import com.zhangke.fread.status.richtext.android.HtmlParser
import com.zhangke.fread.status.richtext.android.span.CustomEmojiSpan
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class FreadScreen : Screen {

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

    private val detailRole = """
        {"accountUri":{"host":"activitypub.com","rawPath":"/user","queries":{"finger":"@AtomZ@m.cmx.im","baseUrl":"https://m.cmx.im"}},"baseUrl":null}
    """.trimIndent()
    private val detailStatus = """
        {"type":"com.zhangke.fread.status.status.model.Status.NewBlog","blog":{"id":"112047716002221579","author":{"uri":{"host":"activitypub.com","rawPath":"/user","queries":{"finger":"@AtomZ@m.cmx.im","baseUrl":"https://m.cmx.im"}},"webFinger":{"name":"AtomZ","host":"m.cmx.im"},"name":"Apeironisim","description":"<p>未经审视的人生是不值得过的</p>","avatar":"https://media.cmx.edu.kg/accounts/avatars/109/305/640/413/684/932/original/c7b0611472c5849e.webp"},"title":null,"content":"<p><a href=\"https://m.cmx.im/tags/%E6%BC%82%E4%BA%AE%E7%9A%84%E5%B0%8F%E7%8E%A9%E6%84%8F\" class=\"mention hashtag\" rel=\"tag\">#<span>漂亮的小玩意</span></a><br />前几天网上看到个很复古的老式面包机，完全长在我审美上了，搜了半天找到个专门卖复古面包机的网站，但貌似只能发货到美国 :awesome: ，但真的好喜欢这个小玩意。<br /><a href=\"https://www.toastercentral.com/index.htm\" target=\"_blank\" rel=\"nofollow noopener noreferrer\" translate=\"no\"><span class=\"invisible\">https://www.</span><span class=\"\">toastercentral.com/index.htm</span><span class=\"invisible\"></span></a></p>","date":1709712463415,"forwardCount":0,"likeCount":3,"repliesCount":0,"sensitive":false,"spoilerText":"","platform":{"uri":"freadapp://activitypub.com/platform?serverBaseUrl=https%3A%2F%2Fm.cmx.im","name":"长毛象中文站","description":"长毛象中文站是一个开放、友好、有爱的社区。长毛象中文站主题为喵、汪、各种动物、社交、科技、编程及生活。我们欢迎友好、热情、乐于分享的朋友，无论你的兴趣点是什么。","baseUrl":{"scheme":"https","host":"m.cmx.im"},"protocol":{"id":"ActivityPub","name":"Mastodon"},"thumbnail":"https://m.cmx.im/packs/media/images/preview-6399aebd96ccf025654e2977454f168f.png"},"mediaList":[{"id":"112047712343519435","url":"https://media.cmx.edu.kg/media_attachments/files/112/047/712/343/519/435/original/ca37da636dd1d209.jpeg","type":"IMAGE","previewUrl":"https://media.cmx.edu.kg/media_attachments/files/112/047/712/343/519/435/small/ca37da636dd1d209.jpeg","remoteUrl":null,"description":null,"blurhash":"","meta":{"type":"com.zhangke.fread.status.blog.BlogMediaMeta.ImageMeta","original":{"width":1042,"height":1172,"size":"1042x1172","aspect":0.8890785},"small":{"width":1042,"height":1172,"size":"1042x1172","aspect":0.8890785},"focus":null}}],"emojis":[{"shortcode":"awesome","url":"https://media.cmx.edu.kg/custom_emojis/images/000/067/590/original/72ae4469639d0a2e.png","staticUrl":"https://media.cmx.edu.kg/custom_emojis/images/000/067/590/static/72ae4469639d0a2e.png"}],"mentions":[],"poll":null},"supportInteraction":[{"type":"com.zhangke.fread.status.status.model.StatusInteraction.Like","likeCount":3,"liked":false,"enable":true},{"type":"com.zhangke.fread.status.status.model.StatusInteraction.Forward","forwardCount":0,"forwarded":false,"enable":true},{"type":"com.zhangke.fread.status.status.model.StatusInteraction.Comment","commentCount":0,"enable":true},{"type":"com.zhangke.fread.status.status.model.StatusInteraction.Bookmark","bookmarkCount":null,"bookmarked":false,"enable":true},{"type":"com.zhangke.fread.status.status.model.StatusInteraction.Delete","enable":true}]}
    """.trimIndent()

    @Composable
    fun LoadingPage(
        loadMoreState: LoadState,
    ){
        val state = rememberLazyListState()
        val layoutInfo by remember { derivedStateOf { state.layoutInfo } }
        val rememberedLoadMoreState by remember(loadMoreState) {
            mutableStateOf(loadMoreState)
        }
        LazyColumn(
            state = state,
        ) {
            items(100) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center),
                        text = "$it"
                    )
                }
            }
            item {
                LoadMoreUi(loadState = rememberedLoadMoreState) {

                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
    @Composable
    override fun Content() {
        MainPage()

//        LoadingPage(loadMoreState = LoadState.Idle)

//        RichTextPreview()

//        AndroidRichTextPreview()
//        Box(modifier = Modifier.fillMaxSize()) {
//            val navigator = LocalNavigator.currentOrThrow
//            Button(onClick = {
//                val role = Json.decodeFromString<IdentityRole>(detailRole)
//                val status = Json.decodeFromString<Status>(detailStatus)
//                navigator.push(
//                    StatusContextScreen(
//                        role = role,
//                        status = status,
//                    )
//                )
//            }) {
//                Text(text = "GO")
//            }
//        }
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
                HashtagInStatus(
                    name = "漂亮的小玩意",
                    url = "https://m.cmx.im/tags/%E6%BC%82%E4%BA%AE%E7%9A%84%E5%B0%8F%E7%8E%A9%E6%84%8F",
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
