package com.zhangke.utopia.pages

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.ScreenModelFactory
import cafe.adriel.voyager.hilt.getViewModel
import com.zhangke.utopia.pages.main.MainPage
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel

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

    @Composable
    private fun RichTextPreview() {
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
    }

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
    @Composable
    override fun Content() {
        MainPage()

//        RichTextPreview()

//        Column(
//            modifier = Modifier.fillMaxSize(),
//            verticalArrangement = Arrangement.Center,
//        ) {
//            TextBitmap("张")
//            Spacer(modifier = Modifier.height(16.dp))
//            TextBitmap("张可")
//            Spacer(modifier = Modifier.height(16.dp))
//            TextBitmap("es")
//            Spacer(modifier = Modifier.height(16.dp))
//            TextBitmap("ES")
//            Spacer(modifier = Modifier.height(16.dp))
//            TextBitmap("E")
//        }

//            Navigator(TabTestScreen())

//        val tabs: List<PagerTab> = remember {
//            listOf(
//                FirstTab(0),
//                FirstTab(1),
//                SecondTab(2),
//                ThirdTab(3),
//            )
//        }
//        val pagerState = rememberPagerState {
//            tabs.size
//        }
//            Column(modifier = Modifier.fillMaxSize()) {
//                Row(modifier = Modifier.fillMaxWidth()) {
//                    Button(onClick = {
//                        tabNavigator.current = tabs.first()
//                    }) {
//                        Text(text = "First")
//                    }
//                    Button(onClick = {
//                        tabNavigator.current = tabs[1]
//                    }) {
//                        Text(text = "Second")
//                    }
//                    Button(onClick = {
//                        tabNavigator.current = tabs[2]
//                    }) {
//                        Text(text = "Third")
//                    }
//                }
//                Box(modifier = Modifier.fillMaxSize()) {
//                    CurrentTab()
//                }
//            }
//        HorizontalPager(
//            modifier = Modifier.fillMaxSize(),
//            state = pagerState,
//        ) {
//            Log.d("U_TEST", "current page index is $it")
//            with(tabs[it]) {
//                TabContent()
//            }
//        }
    }
}

interface PagerTab {

    val title: String
        @Composable get

    @Composable
    fun Screen.TabContent()
}

class FirstTab(private val pageIndex: Int) : PagerTab {

    override val title: String
        @Composable get() = "$pageIndex"

    @OptIn(ExperimentalVoyagerApi::class)
    @Composable
    override fun Screen.TabContent() {
        val viewModel = getViewModel<FirstViewModel, FirstViewModel.Factory> {
            it.create(pageIndex)
        }
        Box(modifier = Modifier.fillMaxSize()) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = title,
            )
        }
    }
}

@HiltViewModel(assistedFactory = FirstViewModel.Factory::class)
class FirstViewModel @AssistedInject constructor(
    @Assisted val pageIndex: Int,
) : ViewModel() {

    @AssistedFactory
    interface Factory : ScreenModelFactory {
        fun create(pageIndex: Int): FirstViewModel
    }

    init {
        Log.d("U_TEST", "FirstViewModel@${hashCode()} init, index is $pageIndex")
    }
}

class SecondTab(private val pageIndex: Int) : PagerTab {

    override val title: String
        @Composable get() = "$pageIndex"

    @OptIn(ExperimentalVoyagerApi::class)
    @Composable
    override fun Screen.TabContent() {
        val viewModel: SecondViewModel = getViewModel<SecondViewModel, SecondViewModel.Factory> {
            it.create(pageIndex)
        }
        Box(modifier = Modifier.fillMaxSize()) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = "Second",
            )
        }
    }
}

@HiltViewModel(assistedFactory = SecondViewModel.Factory::class)
class SecondViewModel @AssistedInject constructor(
    @Assisted val pageIndex: Int,
) : ViewModel() {

    @AssistedFactory
    interface Factory : ScreenModelFactory {
        fun create(pageIndex: Int): SecondViewModel
    }

    init {
        Log.d("U_TEST", "SecondViewModel@${hashCode()} init, index is $pageIndex")
    }
}

class ThirdTab(private val pageIndex: Int) : PagerTab {

    override val title: String
        @Composable get() = "$pageIndex"

    @OptIn(ExperimentalVoyagerApi::class)
    @Composable
    override fun Screen.TabContent() {
        val viewModel: ThirdViewModel = getViewModel<ThirdViewModel, ThirdViewModel.Factory> {
            it.create(pageIndex)
        }
        Box(modifier = Modifier.fillMaxSize()) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = "Third",
            )
        }
    }
}

@HiltViewModel(assistedFactory = ThirdViewModel.Factory::class)
class ThirdViewModel @AssistedInject constructor(
    @Assisted val pageIndex: Int,
) : ViewModel() {

    @AssistedFactory
    interface Factory : ScreenModelFactory {
        fun create(pageIndex: Int): ThirdViewModel
    }

    init {
        Log.d("U_TEST", "ThirdViewModel@${hashCode()} init, index is $pageIndex")
    }
}

//@Module
//@InstallIn(ActivityComponent::class)
//abstract class HiltModule {
//
//    @Binds
//    @IntoMap
//    @ScreenModelFactoryKey(FirstViewModel.Factory::class)
//    abstract fun bindFirstScreenModelFactory(
//        hiltDetailsScreenModelFactory: FirstViewModel.Factory
//    ): ScreenModelFactory
//
//    @Binds
//    @IntoMap
//    @ScreenModelKey(FirstViewModel::class)
//    abstract fun bindFirstScreenModel(testScreenModel: FirstViewModel): ScreenModel
//
//    @Binds
//    @IntoMap
//    @ScreenModelFactoryKey(SecondViewModel.Factory::class)
//    abstract fun bindSecondScreenModelFactory(
//        hiltDetailsScreenModelFactory: SecondViewModel.Factory
//    ): ScreenModelFactory
//
//    @Binds
//    @IntoMap
//    @ScreenModelKey(SecondViewModel::class)
//    abstract fun bindSecondScreenModel(testScreenModel: SecondViewModel): ScreenModel
//
//    @Binds
//    @IntoMap
//    @ScreenModelFactoryKey(ThirdViewModel.Factory::class)
//    abstract fun bindThirdScreenModelFactory(
//        hiltDetailsScreenModelFactory: ThirdViewModel.Factory
//    ): ScreenModelFactory
//
//    @Binds
//    @IntoMap
//    @ScreenModelKey(ThirdViewModel::class)
//    abstract fun bindThirdScreenModel(testScreenModel: ThirdViewModel): ScreenModel
//}
