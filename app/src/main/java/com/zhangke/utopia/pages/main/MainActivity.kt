package com.zhangke.utopia.pages.main

import android.os.Bundle
import android.widget.FrameLayout
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.zhangke.framework.architect.theme.UtopiaTheme
import com.zhangke.utopia.blogprovider.BlogFeedsShell
import com.zhangke.utopia.composable.Toolbar
import com.zhangke.utopia.pages.feeds.FeedsFragment
import com.zhangke.utopia.pages.providermanager.AddProviderFragment

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            UtopiaTheme {
                val pageState = viewModel.pageState.collectAsState().value
                val feedsShellList: List<BlogFeedsShell> =
                    viewModel.feedsShellFlow.collectAsState(initial = emptyList()).value
                MainPage(
                    pageState = pageState,
                    feedsShellList = feedsShellList,
                )
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MainPage(
        pageState: MainViewModel.PageState,
        feedsShellList: List<BlogFeedsShell>
    ) {
        Scaffold(
            topBar = {
                Toolbar(title = "MainPage")
            }
        ) {
            when (pageState) {
                MainViewModel.PageState.ADD_SERVER -> {
                    FragmentComposable("add_server_fragment") {
                        AddProviderFragment.newInstance()
                    }
                }
                MainViewModel.PageState.FEEDS -> {
                    if (feedsShellList.isNotEmpty()) {
                        FragmentComposable("feeds_fragment") {
                            FeedsFragment.newInstance(feedsShellList.first().id)
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun FragmentComposable(
        tag: String,
        fragmentProvider: () -> Fragment
    ) {
        AndroidView(
            factory = {
                FrameLayout(it).apply {
                    id = ViewCompat.generateViewId()
                }
            },
            update = {
                val fragmentAlreadyAdded =
                    supportFragmentManager.findFragmentByTag(tag) != null
                if (!fragmentAlreadyAdded) {
                    supportFragmentManager.commit {
                        add(it.id, fragmentProvider(), tag)
                    }
                }
            }
        )
    }
}