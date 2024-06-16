package com.zhangke.fread.profile.screen.opensource

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.browser.BrowserLauncher
import com.zhangke.framework.composable.Toolbar
import com.zhangke.fread.profile.R

class OpenSourceScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current
        Scaffold(
            topBar = {
                Toolbar(
                    title = stringResource(R.string.profile_setting_open_source_title),
                    onBackClick = navigator::pop,
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            ) {
                val openSourceInfoList = remember {
                    buildOpenSourceInfoList()
                }
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    items(openSourceInfoList) { openSourceInfo ->
                        OpenSourceItem(
                            openSource = openSourceInfo,
                            onClick = {
                                BrowserLauncher.launchWebTabInApp(context, it.url)
                            },
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun OpenSourceItem(
        openSource: OpenSourceInfo,
        onClick: (OpenSourceInfo) -> Unit,
    ) {
        Column(
            modifier = Modifier
                .clickable { onClick(openSource) }
                .fillMaxWidth()
                .padding(top = 8.dp),
        ) {
            Text(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                text = "${openSource.name} - ${openSource.author}",
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                modifier = Modifier.padding(start = 16.dp, top = 4.dp, end = 16.dp),
                text = openSource.license,
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                modifier = Modifier.padding(start = 16.dp, top = 4.dp, end = 16.dp),
                text = openSource.url,
                maxLines = 1,
                style = MaterialTheme.typography.bodyMedium,
            )
            HorizontalDivider(modifier = Modifier.padding(top = 8.dp))
        }
    }

    private fun buildOpenSourceInfoList(): List<OpenSourceInfo> {
        return listOf(
            OpenSourceInfo(
                name = "KRouter",
                author = "ZhangKe",
                license = "The Apache Software License, Version 2.0",
                url = "https://github.com/0xZhangKe/KRouter"
            ),
            OpenSourceInfo(
                name = "Filt",
                author = "ZhangKe",
                license = "The Apache Software License, Version 2.0",
                url = "https://github.com/0xZhangKe/Filt"
            ),
            OpenSourceInfo(
                name = "ActivityPub-Kotlin",
                author = "ZhangKe",
                license = "The Apache Software License, Version 2.0",
                url = "https://github.com/0xZhangKe/ActivityPub-Kotlin"
            ),
            OpenSourceInfo(
                name = "Kotlin",
                author = "Jetbrains",
                license = "The Apache Software License, Version 2.0",
                url = "https://kotlinlang.org/"
            ),
            OpenSourceInfo(
                name = "Jetpack Compose",
                author = "Google",
                license = "The Apache Software License, Version 2.0",
                url = "https://developer.android.com/jetpack/androidx/releases/compose"
            ),
            OpenSourceInfo(
                name = "Voyager",
                author = "Adriel Café",
                license = "The MIT License (MIT)",
                url = "https://voyager.adriel.cafe/"
            ),
            OpenSourceInfo(
                name = "Jetpack",
                author = "Google",
                license = "The Apache Software License, Version 2.0",
                url = "https://developer.android.com/jetpack"
            ),
            OpenSourceInfo(
                name = "AndroidX",
                author = "Google",
                license = "The Apache Software License, Version 2.0",
                url = "https://developer.android.com/jetpack/androidx/"
            ),
            OpenSourceInfo(
                name = "RxJava",
                author = "ReactiveX",
                license = "The Apache Software License, Version 2.0",
                url = "https://github.com/ReactiveX/RxJava"
            ),
            OpenSourceInfo(
                name = "Gson",
                author = "Google",
                license = "The Apache Software License, Version 2.0",
                url = "https://github.com/google/gson/"
            ),
            OpenSourceInfo(
                name = "OkHttp",
                author = "Square",
                license = "The Apache Software License, Version 2.0",
                url = "https://square.github.io/okhttp/"
            ),
            OpenSourceInfo(
                name = "Retrofit",
                author = "Square",
                license = "The Apache Software License, Version 2.0",
                url = "https://github.com/square/retrofit"
            ),
            OpenSourceInfo(
                name = "Accompanist",
                author = "Google",
                license = "The Apache Software License, Version 2.0",
                url = "https://github.com/google/accompanist"
            ),
            OpenSourceInfo(
                name = "Material-Components",
                author = "Google",
                license = "The Apache Software License, Version 2.0",
                url = "https://github.com/material-components/material-components-android"
            ),
            OpenSourceInfo(
                name = "Coil",
                author = "Coil",
                license = "The Apache Software License, Version 2.0",
                url = "https://github.com/coil-kt/coil"
            ),
        )
    }

    data class OpenSourceInfo(
        val name: String,
        val author: String,
        val license: String,
        val url: String,
    )
}
