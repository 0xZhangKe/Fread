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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.composable.currentOrThrow
import com.zhangke.framework.nav.LocalNavBackStack
import com.zhangke.fread.common.browser.LocalActivityBrowserLauncher
import com.zhangke.fread.common.browser.launchWebTabInApp
import com.zhangke.fread.localization.LocalizedString
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource

@Serializable
object OpenSourceScreenNavKey : NavKey

@Composable
fun OpenSourceScreen() {
    val backStack = LocalNavBackStack.currentOrThrow
    val browserLauncher = LocalActivityBrowserLauncher.current
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            Toolbar(
                title = stringResource(LocalizedString.profileSettingOpenSourceTitle),
                onBackClick = backStack::removeLastOrNull,
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
                            browserLauncher.launchWebTabInApp(coroutineScope, it.url)
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
            name = "Fread",
            author = "ZhangKe",
            license = OpenSourceInfo.LICENSE_APACHE_2,
            url = "https://github.com/0xZhangKe/Fread"
        ),
        OpenSourceInfo(
            name = "KRouter",
            author = "ZhangKe",
            license = OpenSourceInfo.LICENSE_APACHE_2,
            url = "https://github.com/0xZhangKe/KRouter"
        ),
        OpenSourceInfo(
            name = "Filt",
            author = "ZhangKe",
            license = OpenSourceInfo.LICENSE_APACHE_2,
            url = "https://github.com/0xZhangKe/Filt"
        ),
        OpenSourceInfo(
            name = "ActivityPub-Kotlin",
            author = "ZhangKe",
            license = OpenSourceInfo.LICENSE_APACHE_2,
            url = "https://github.com/0xZhangKe/ActivityPub-Kotlin"
        ),
        OpenSourceInfo(
            name = "Kotlin",
            author = "Jetbrains",
            license = OpenSourceInfo.LICENSE_APACHE_2,
            url = "https://kotlinlang.org/"
        ),
        OpenSourceInfo(
            name = "Jetpack Compose",
            author = "Google",
            license = OpenSourceInfo.LICENSE_APACHE_2,
            url = "https://developer.android.com/jetpack/androidx/releases/compose"
        ),
        OpenSourceInfo(
            name = "Jetpack",
            author = "Google",
            license = OpenSourceInfo.LICENSE_APACHE_2,
            url = "https://developer.android.com/jetpack"
        ),
        OpenSourceInfo(
            name = "AndroidX",
            author = "Google",
            license = OpenSourceInfo.LICENSE_APACHE_2,
            url = "https://developer.android.com/jetpack/androidx/"
        ),
        OpenSourceInfo(
            name = "Gson",
            author = "Google",
            license = OpenSourceInfo.LICENSE_APACHE_2,
            url = "https://github.com/google/gson/"
        ),
        OpenSourceInfo(
            name = "OkHttp",
            author = "Square",
            license = OpenSourceInfo.LICENSE_APACHE_2,
            url = "https://square.github.io/okhttp/"
        ),
        OpenSourceInfo(
            name = "Retrofit",
            author = "Square",
            license = OpenSourceInfo.LICENSE_APACHE_2,
            url = "https://github.com/square/retrofit"
        ),
        OpenSourceInfo(
            name = "Accompanist",
            author = "Google",
            license = OpenSourceInfo.LICENSE_APACHE_2,
            url = "https://github.com/google/accompanist"
        ),
        OpenSourceInfo(
            name = "Material-Components",
            author = "Google",
            license = OpenSourceInfo.LICENSE_APACHE_2,
            url = "https://github.com/material-components/material-components-android"
        ),
        OpenSourceInfo(
            name = "compose-richtext",
            author = "halilozercan",
            license = OpenSourceInfo.LICENSE_APACHE_2,
            url = "https://github.com/halilozercan/compose-richtext",
        ),
        OpenSourceInfo(
            name = "ComposeReorderable",
            author = "André Claßen",
            license = OpenSourceInfo.LICENSE_APACHE_2,
            url = "https://github.com/aclassen/ComposeReorderable",
        ),
        OpenSourceInfo(
            name = "compose-wheel-picker",
            author = "zj565061763",
            license = "MIT license",
            url = "https://github.com/zj565061763/compose-wheel-picker",
        ),
    )
}

data class OpenSourceInfo(
    val name: String,
    val author: String,
    val license: String,
    val url: String,
) {

    companion object {

        const val LICENSE_APACHE_2 = "The Apache Software License, Version 2.0"
    }
}
