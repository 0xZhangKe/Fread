package com.zhangke.utopia.pages

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.FontMetricsInt
import android.graphics.drawable.Drawable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ReplacementSpan
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
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
import com.zhangke.framework.utils.appContext
import com.zhangke.utopia.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
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
//        MainPage()

//        RichTextPreview()

        AndroidRichTextPreview()
    }

    @Composable
    private fun AndroidRichTextPreview() {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(100.dp))

            val coroutineScope = rememberCoroutineScope()
            AndroidView(
                factory = {
                    val view = TextView(it)
                    view.textSize = 14F
                    view.text = buildSpan(coroutineScope, view)
                    view
                },
            )
        }
    }

    private fun buildSpan(
        coroutineScope: CoroutineScope,
        textView: TextView,
    ): SpannableStringBuilder {
        val spanBuilder = SpannableStringBuilder()
        spanBuilder.append("123")
        spanBuilder.setSpan(
            ImageSpan(coroutineScope, textView),
            1,
            2,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE,
        )
        spanBuilder.append("abc")
        return spanBuilder
    }

    class ImageSpan(
        private val coroutineScope: CoroutineScope,
        private val view: TextView,
    ) : ReplacementSpan() {

        private var drawable: Drawable? = null

        init {
            coroutineScope.launch {
                delay(3000)
                drawable =
                    AppCompatResources.getDrawable(appContext, R.drawable.ic_launcher_foreground)
                view.invalidate()
            }
        }

        override fun getSize(
            paint: Paint,
            text: CharSequence?,
            start: Int,
            end: Int,
            fm: FontMetricsInt?
        ): Int {
            return Math.round(paint.descent() - paint.ascent())
        }

        override fun draw(
            canvas: Canvas,
            text: CharSequence?,
            start: Int,
            end: Int,
            x: Float,
            top: Int,
            y: Int,
            bottom: Int,
            paint: Paint
        ) {
            val drawable = drawable
            val size = Math.round(paint.descent() - paint.ascent())
            if (drawable == null) {
                val alpha = paint.alpha
                paint.setAlpha(alpha shr 1)
                canvas.drawRoundRect(
                    x,
                    top.toFloat(),
                    x + size,
                    (top + size).toFloat(),
                    dpToPx(2F),
                    dpToPx(2F),
                    paint
                )
                paint.setAlpha(alpha)
            } else {
                // AnimatedImageDrawable doesn't like when its bounds don't start at (0, 0)
                val bounds = drawable.getBounds()
                val dw = drawable.intrinsicWidth
                val dh = drawable.intrinsicHeight
                if (bounds.left != 0 || bounds.top != 0 || bounds.right != dw || bounds.left != dh) {
                    drawable.setBounds(0, 0, dw, dh)
                }
                canvas.save()
                canvas.translate(x, top.toFloat())
                canvas.scale(size / dw.toFloat(), size / dh.toFloat(), 0f, 0f)
                drawable.draw(canvas)
                canvas.restore()
            }
        }

        private fun dpToPx(dp: Float): Float {
            return Math.round(dp * appContext.resources.displayMetrics.density).toFloat()
        }
    }
}
