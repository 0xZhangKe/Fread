package com.zhangke.utopia.providermanager

import android.os.Bundle
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Scaffold
import androidx.compose.material.TextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import coil.compose.AsyncImage
import com.zhangke.activitypub.entry.ActivityPubInstance
import com.zhangke.framework.architect.theme.UtopiaTheme
import com.zhangke.utopia.R
import com.zhangke.utopia.composable.Toolbar
import kotlinx.coroutines.launch

class AddProviderActivity : AppCompatActivity() {

    private val viewModel: AddProviderViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            UtopiaTheme {
                val pageState = viewModel.pageState.collectAsState().value
                val activityPubInstance =
                    if (pageState == AddProviderViewModel.PageState.ACTIVITY_PUB_INFO) {
                        viewModel.requireActivityPubInstance()
                    } else {
                        null
                    }
                AddProviderPage(
                    pageState = pageState,
                    activityPubInstance = activityPubInstance,
                    onAddClick = {
                        viewModel.onAddClick(it)
                    },
                    navigationBackClick = ::onNavigationBackClick,
                    onConfirmAddClick = {

                    }
                )
            }
        }
        onBackPressedDispatcher.addCallback(this) {
            onNavigationBackClick()
        }
    }

    private fun onNavigationBackClick() {
        lifecycleScope.launch {
            if (viewModel.pageState.value != AddProviderViewModel.PageState.INITIALIZE) {
                viewModel.moveToInitializedPage()
            } else {
                finish()
            }
        }
    }

    @Preview
    @Composable
    fun PreviewAddProviderPage() {
        AddProviderPage(
            AddProviderViewModel.PageState.INITIALIZE,
            null,
            onAddClick = {},
            navigationBackClick = {},
            onConfirmAddClick = {}
        )
    }

    @Preview
    @Composable
    fun PreviewActivityPubPage() {
        val instance = ActivityPubInstance(
            domain = "musician.social",
            title = "musician",
            version = "1",
            sourceUrl = null,
            description = "MUSICIAN.SOCIAL is an server focused on Musicians who create, play, or love #jazz, #rock, #pop, #indie, #classical and all other types of #music",
            usage = ActivityPubInstance.Usage(ActivityPubInstance.Usage.Users(activeMonth = 681721)),
            languages = listOf("en"),
            rules = emptyList(),
            thumbnail = ActivityPubInstance.Thumbnail(url = "https://proxy.joinmastodon.org/7c9597cc47c440d758735af7c019d1d0c4189763/68747470733a2f2f66656469686f73742d6d376e2d6d7573696369616e2d6173736574732e73332e75732d776573742d322e616d617a6f6e6177732e636f6d2f66656469686f73742d6d376e2d6d7573696369616e2d6173736574732f736974655f75706c6f6164732f66696c65732f3030302f3030302f3030312f4031782f376364393863306634326331636438632e706e67"),
        )
        AddProviderPage(
            pageState = AddProviderViewModel.PageState.ACTIVITY_PUB_INFO,
            activityPubInstance = instance,
            onAddClick = {},
            navigationBackClick = {},
            onConfirmAddClick = {}
        )
    }

    @Composable
    fun AddProviderPage(
        pageState: AddProviderViewModel.PageState,
        activityPubInstance: ActivityPubInstance?,
        onAddClick: (String) -> Unit,
        navigationBackClick: () -> Unit,
        onConfirmAddClick: () -> Unit,
    ) {
        val pageTitle = when (pageState) {
            AddProviderViewModel.PageState.INITIALIZE -> {
                LocalContext.current.getString(R.string.add_provider_page_title)
            }
            AddProviderViewModel.PageState.ACTIVITY_PUB_INFO -> {
                activityPubInstance!!.title
            }
        }
        Scaffold(
            topBar = {
                Toolbar(
                    title = pageTitle,
                    navigationBackClick = navigationBackClick
                )
            }
        ) {
            when (pageState) {
                AddProviderViewModel.PageState.INITIALIZE -> InitializePage(onAddClick = onAddClick)
                AddProviderViewModel.PageState.ACTIVITY_PUB_INFO -> ActivityPubInstancePage(
                    instance = activityPubInstance!!,
                    onConfirmAddClick = onConfirmAddClick
                )
            }
        }
    }

    @Composable
    fun InitializePage(onAddClick: (String) -> Unit) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.align(Alignment.Center)) {
                var inputtedContent by remember { mutableStateOf("") }
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 50.dp),
                    value = inputtedContent,
                    onValueChange = {
                        inputtedContent = it
                    }
                )

                Button(
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .align(Alignment.CenterHorizontally),
                    onClick = { onAddClick(inputtedContent) }
                ) {
                    Text(text = LocalContext.current.getString(R.string.add))
                }
            }
        }
    }

    @Composable
    fun ActivityPubInstancePage(
        instance: ActivityPubInstance,
        onConfirmAddClick: () -> Unit,
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Card(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth()
                    .padding(horizontal = 60.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(15.dp)
                ) {
                    AsyncImage(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1.5F),
                        model = instance.thumbnail.url,
                        contentDescription = "cover"
                    )

                    Text(
                        modifier = Modifier.padding(top = 10.dp),
                        text = instance.domain,
                        fontSize = 12.sp
                    )

                    Text(
                        modifier = Modifier.padding(top = 10.dp),
                        text = instance.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )

                    Text(
                        modifier = Modifier.padding(top = 10.dp),
                        text = instance.description,
                        fontSize = 14.sp
                    )

                    Button(
                        modifier = Modifier.padding(top = 10.dp),
                        onClick = onConfirmAddClick
                    ) {
                        Text(
                            text = LocalContext.current.getString(R.string.add_provider_page_confirm_button)
                        )
                    }
                }
            }
        }
    }
}