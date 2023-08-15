package com.zhangke.utopia.status.ui

import android.text.Html
import android.widget.TextView
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.text.HtmlCompat
import coil.compose.AsyncImage
import com.zhangke.framework.utils.WebFinger
import com.zhangke.utopia.status.blog.Blog
import com.zhangke.utopia.status.uri.StatusProviderUri
import com.zhangke.utopia.status.user.UtopiaUser
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.concurrent.getOrSet

@Composable
fun BlogContentComposable(
    modifier: Modifier = Modifier,
    blog: Blog
) {
    Card(
        modifier = modifier
    ) {
        ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
            val (avatar, name, guideline, dateTime, userId) = createRefs()
            AsyncImage(
                modifier = Modifier
                    .size(40.dp)
                    .constrainAs(avatar) {
                        top.linkTo(parent.top, 10.dp)
                        start.linkTo(parent.start, 10.dp)
                        bottom.linkTo(parent.bottom)
                    },
                model = blog.author.avatar,
                contentDescription = "avatar"
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
                text = blog.author.userName,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                modifier = Modifier.constrainAs(dateTime) {
                    start.linkTo(name.end, margin = 5.dp)
                    baseline.linkTo(name.baseline)
                },
                text = remember(blog) { formatStatusDateTime(blog.date) },
                fontSize = 10.sp,
            )

            Text(
                modifier = Modifier.constrainAs(userId) {
                    top.linkTo(guideline.bottom, 3.dp)
                    start.linkTo(name.start)
                },
                text = blog.author.webFinger.toString(),
                fontSize = 12.sp,
            )
        }
        AndroidView(
            modifier = Modifier.padding(start = 15.dp, end = 15.dp, top = 8.dp, bottom = 10.dp),
            factory = {
                TextView(it).apply {
                    textSize = 14F
                }
            },
            update = {
                it.text = HtmlCompat.fromHtml(blog.content, Html.FROM_HTML_MODE_LEGACY)
            }
        )
    }
}

private val statusDateFormatLocal = ThreadLocal<SimpleDateFormat>()

private fun formatStatusDateTime(date: Date): String {
    val format = statusDateFormatLocal.getOrSet { SimpleDateFormat("MM-dd HH:mm:ss", Locale.ROOT) }
    return format.format(date)
}

@Preview
@Composable
private fun PreviewBlogContentComposable() {
    val author = UtopiaUser(
        userName = "zhangke",
        webFinger = WebFinger.create("@zhangke@m.cmx.im")!!,
        uri = StatusProviderUri.create("@zhangke@m.cmx.im")!!,
        description = "一个落魄Android开发",
        avatar = "https://media.cmx.edu.kg/accounts/avatars/109/305/640/413/684/932/original/2804adcd878c37c9.png",
        homePageUrl = "https://m.cmx.im/@AtomZ",
        header = "",
        followersCount = 100,
        followingCount = 234,
        statusesCount = 23412,
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
        repliesCount = 10,
    )
    BlogContentComposable(blog = blog)
}