package com.zhangke.utopia.pages.main

import android.os.Bundle
import android.widget.FrameLayout
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.zhangke.framework.architect.theme.UtopiaTheme
import com.zhangke.utopia.pages.UtopiaRouters
import com.zhangke.utopia.pages.feeds.registerFeedsNavigation
import com.zhangke.utopia.pages.sources.registerSourcesNavigation

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            UtopiaTheme {

            }
        }
    }

    @Composable
    private fun MainPage() {
        val navController = rememberNavController()
        val rootRouters = remember {
            UtopiaRouters()
        }
        NavHost(
            navController = navController,
            startDestination = rootRouters.root,
        ) {
            with(rootRouters) {
                registerFeedsNavigation()
                registerSourcesNavigation()
            }
        }
    }

//    @OptIn(ExperimentalMaterial3Api::class)
//    @Composable
//    fun MainPage(
//        pageState: MainViewModel.PageState,
//        feedsShellList: List<Int>
//    ) {
//        Scaffold(
//            topBar = {
//                Toolbar(title = "MainPage")
//            }
//        ) {
//            when (pageState) {
//                MainViewModel.PageState.ADD_SERVER -> {
//                    FragmentComposable("add_server_fragment") {
//                        AddProviderFragment.newInstance()
//                    }
//                }
//                MainViewModel.PageState.FEEDS -> {
//                    if (feedsShellList.isNotEmpty()) {
//                        FragmentComposable("feeds_fragment") {
//                            FeedsFragment.newInstance(feedsShellList.first())
//                        }
//                    }
//                }
//            }
//        }
//    }

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