package com.zhangke.utopia.status.ui

import android.widget.TextView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.text.HtmlCompat
import coil.compose.AsyncImage
import com.zhangke.framework.utils.WebFinger
import com.zhangke.utopia.status.author.BlogAuthor
import com.zhangke.utopia.status.blog.Blog
import com.zhangke.utopia.status.status.model.StatusAction
import com.zhangke.utopia.status.ui.action.StatusActionPanel
import com.zhangke.utopia.status.ui.image.OnBlogMediaClick
import com.zhangke.utopia.status.ui.poll.BlogPoll
import com.zhangke.utopia.status.uri.FormalUri
import com.zhangke.utopia.statusui.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.concurrent.getOrSet

@Composable
fun BlogContentUi(
    modifier: Modifier = Modifier,
    blog: Blog,
    supportActions: List<StatusAction>,
    indexInList: Int,
    onMediaClick: OnBlogMediaClick,
    reblogAuthor: BlogAuthor? = null,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
                val (avatar, name, guideline, dateTime, userId) = createRefs()
                BlogAuthorAvatar(
                    modifier = Modifier
                        .size(40.dp)
                        .constrainAs(avatar) {
                            top.linkTo(parent.top, 10.dp)
                            start.linkTo(parent.start, 8.dp)
                            bottom.linkTo(parent.bottom)
                        },
                    reblogAvatar = reblogAuthor?.avatar,
                    authorAvatar = blog.author.avatar,
                )

                Spacer(
                    modifier = Modifier
                        .height(1.dp)
                        .fillMaxWidth()
                        .constrainAs(guideline) {
                            top.linkTo(avatar.top)
                            bottom.linkTo(avatar.bottom)
                            start.linkTo(parent.start)
                        }
                )

                Text(
                    modifier = Modifier.constrainAs(name) {
                        start.linkTo(avatar.end, margin = 8.dp)
                        bottom.linkTo(guideline.top)
                    },
                    text = blog.author.name,
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    modifier = Modifier.constrainAs(dateTime) {
                        start.linkTo(name.start)
                        top.linkTo(name.bottom, 4.dp)
                    },
                    text = remember(blog) { formatStatusDateTime(blog.date) },
                    style = MaterialTheme.typography.bodySmall,
                )

                Text(
                    modifier = Modifier.constrainAs(userId) {
                        baseline.linkTo(dateTime.baseline)
                        start.linkTo(dateTime.end, 2.dp)
                    },
                    text = blog.author.webFinger.toString(),
                    fontSize = 12.sp,
                )
            }
            val sensitive = blog.sensitive
            val spoilerText = blog.spoilerText
            val canHidden = blog.sensitive || spoilerText.isNotEmpty()
            var hideContent by rememberSaveable {
                mutableStateOf(canHidden)
            }
            if (spoilerText.isNotEmpty()) {
                AndroidView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(start = 15.dp, end = 15.dp, top = 8.dp),
                    factory = {
                        TextView(it).apply {
                            textSize = 14F
                        }
                    },
                    update = {
                        it.text = HtmlCompat.fromHtml(
                            spoilerText,
                            HtmlCompat.FROM_HTML_MODE_COMPACT,
                        )
                    }
                )
            }
            val hasContent = blog.content.isNotEmpty()
            if (hasContent) {
                if (hideContent) {
                    TextButton(
                        onClick = {
                            hideContent = false
                        },
                        colors = ButtonDefaults.textButtonColors(
                            containerColor = Color.Transparent,
                        )
                    ) {
                        Text(text = stringResource(R.string.status_ui_image_content_show_hidden_label))
                    }
                }
                if (!hideContent) {
                    AndroidView(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(start = 15.dp, end = 15.dp, top = 8.dp),
                        factory = {
                            TextView(it).apply {
                                textSize = 14F
                            }
                        },
                        update = {
                            it.text = HtmlCompat.fromHtml(
                                blog.content,
                                HtmlCompat.FROM_HTML_MODE_COMPACT,
                            )
                        }
                    )
                    if (canHidden) {
                        TextButton(
                            onClick = {
                                hideContent = true
                            },
                            colors = ButtonDefaults.textButtonColors(
                                containerColor = Color.Transparent,
                            )
                        ) {
                            Text(text = stringResource(R.string.status_ui_image_content_hide_hidden_label))
                        }
                    }
                }
            }

            if (blog.mediaList.isNotEmpty()) {
                BlogMedias(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp, top = 6.dp, end = 8.dp, bottom = 8.dp),
                    mediaList = blog.mediaList,
                    indexInList = indexInList,
                    sensitive = sensitive,
                    onMediaClick = onMediaClick,
                )
            }
            if (blog.poll != null) {
                BlogPoll(
                    modifier = Modifier.fillMaxWidth(),
                    poll = blog.poll!!,
                    onVote = {},
                )
            }
            StatusActionPanel(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, end = 8.dp, bottom = 8.dp),
                actions = supportActions,
            )
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(0.5.dp)
            )
        }
    }
}

private val statusDateFormatLocal = ThreadLocal<SimpleDateFormat>()

private fun formatStatusDateTime(date: Date): String {
    val format = statusDateFormatLocal.getOrSet { SimpleDateFormat("MM-dd HH:mm:ss", Locale.ROOT) }
    return format.format(date)
}

@Composable
private fun BlogAuthorAvatar(
    modifier: Modifier,
    reblogAvatar: String?,
    authorAvatar: String?,
) {
    if (reblogAvatar.isNullOrEmpty()) {
        BlogAuthorAvatar(
            modifier = modifier,
            imageUrl = authorAvatar,
        )
    } else {
        Box(modifier = modifier) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(end = 6.dp, bottom = 6.dp)
            ) {
                BlogAuthorAvatar(
                    modifier = Modifier.fillMaxSize(),
                    imageUrl = authorAvatar,
                )
            }
            BlogAuthorAvatar(
                modifier = Modifier
                    .size(18.dp)
                    .align(Alignment.BottomEnd),
                imageUrl = reblogAvatar,
            )
        }
    }
}

@Composable
private fun BlogAuthorAvatar(
    modifier: Modifier,
    imageUrl: String?,
) {
    AsyncImage(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp)),
        model = imageUrl,
//        error = Icons.Default.AccountCircle,
//        placeholder = Icons.Default.AccountCircle,
        contentDescription = null,
    )
}

@Preview
@Composable
private fun PreviewBlogContentComposable() {
    val author = BlogAuthor(
        name = "zhangke",
        webFinger = WebFinger.create("@zhangke@m.cmx.im")!!,
        uri = FormalUri.from("@zhangke@m.cmx.im")!!,
        description = "一个落魄Android开发",
        avatar = "https://media.cmx.edu.kg/accounts/avatars/109/305/640/413/684/932/original/2804adcd878c37c9.png",
    )

    val blog = Blog(
        id = "",
        author = author,
        title = "什么时候才能搞定啊",
        content = "什么时候才能搞定啊, 从去年就开始做了，零零星星的做了好长时间，需求也越做越多，没想到刚开始很简单的想法最后竟然花了这么久才做完，也不知道最终结果如何。",
        mediaList = emptyList(),
        date = Date(),
        forwardCount = 10321,
        likeCount = 38747,
        sensitive = true,
        spoilerText = "",
        repliesCount = 10,
        poll = null,
    )
    BlogContentUi(blog = blog, indexInList = 1, onMediaClick = { _ -> }, supportActions = emptyList())
}