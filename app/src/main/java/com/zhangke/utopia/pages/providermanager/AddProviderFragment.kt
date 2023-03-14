package com.zhangke.utopia.pages.providermanager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Surface
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import coil.compose.AsyncImage
import com.zhangke.framework.architect.coroutines.collectWithLifecycle
import com.zhangke.framework.architect.theme.UtopiaTheme
import com.zhangke.utopia.R
import com.zhangke.utopia.status_provider.StatusSource
import com.zhangke.utopia.status_provider.StatusSourceMaintainer

class AddProviderFragment : Fragment() {

    companion object {

        fun newInstance(): AddProviderFragment {
            return AddProviderFragment()
        }
    }

    private val viewModel: AddProviderViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val naviBackCallback = object : OnBackPressedCallback(false) {
            override fun handleOnBackPressed() {
                onNavigationBackClick()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, naviBackCallback)
        viewModel.pageState.collectWithLifecycle(this) {
            naviBackCallback.isEnabled = it == AddProviderViewModel.PageState.SOURCE_INFO
        }
    }

    private fun onNavigationBackClick() {
        if (viewModel.pageState.value != AddProviderViewModel.PageState.INITIALIZE) {
            viewModel.moveToInitializedPage()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                UtopiaTheme {
                    val pageState = viewModel.pageState.collectAsState().value
                    AddProviderPage(
                        pageState = pageState,
                        statusSourceMaintainer = null,
                        onSearchClick = {
                            viewModel.onSearchClick(it)
                        },
                        onAddSourceClick = {
                            viewModel.onAddSourceServer(it)
                        },
                        onConfirmClick = {
                            viewModel.onConfirm()
                        }
                    )
                }
            }
        }
    }

    @Preview
    @Composable
    fun PreviewAddProviderPage() {
        AddProviderPage(
            AddProviderViewModel.PageState.INITIALIZE,
            null,
            onSearchClick = {},
            onAddSourceClick = {},
            onConfirmClick = {}
        )
    }

    @Preview
    @Composable
    fun PreviewActivityPubPage() {
//        val blogSource = StatusSource(
//            uri = "musician.social",
//            protocol = "activity_pub",
//            sourceName = "musician",
//            sourceDescription = "MUSICIAN.SOCIAL is an server focused on Musicians who create, play, or love #jazz, #rock, #pop, #indie, #classical and all other types of #music",
//            avatar = "https://proxy.joinmastodon.org/7c9597cc47c440d758735af7c019d1d0c4189763/68747470733a2f2f66656469686f73742d6d376e2d6d7573696369616e2d6173736574732e73332e75732d776573742d322e616d617a6f6e6177732e636f6d2f66656469686f73742d6d376e2d6d7573696369616e2d6173736574732f736974655f75706c6f6164732f66696c65732f3030302f3030302f3030312f4031782f376364393863306634326331636438632e706e67",
//            extra = null,
//            metaSourceInfo = MetaSourceInfo(
//                url = "musician.social",
//                name = "Mastodon",
//                description = null,
//                thumbnail = null
//            )
//        )
//        val blogSourceGroup = BlogSourceGroup(
//            metaSourceInfo = MetaSourceInfo(
//                url = "musician.social",
//                name = "musician",
//                description = "MUSICIAN.SOCIAL is an server focused on Musicians who create, play, or love #jazz, #rock, #pop, #indie, #classical and all other types of #music",
//                thumbnail = "https://proxy.joinmastodon.org/7c9597cc47c440d758735af7c019d1d0c4189763/68747470733a2f2f66656469686f73742d6d376e2d6d7573696369616e2d6173736574732e73332e75732d776573742d322e616d617a6f6e6177732e636f6d2f66656469686f73742d6d376e2d6d7573696369616e2d6173736574732f736974655f75706c6f6164732f66696c65732f3030302f3030302f3030312f4031782f376364393863306634326331636438632e706e67",
//            ),
//            sourceList = listOf(blogSource)
//        )
//        AddProviderPage(
//            pageState = AddProviderViewModel.PageState.SOURCE_INFO,
//            blogSourceGroup = blogSourceGroup,
//            onSearchClick = {},
//            onAddSourceClick = {},
//            onConfirmClick = {}
//        )
    }

    @Composable
    fun AddProviderPage(
        pageState: AddProviderViewModel.PageState,
        statusSourceMaintainer: StatusSourceMaintainer?,
        onSearchClick: (String) -> Unit,
        onAddSourceClick: (source: StatusSource) -> Unit,
        onConfirmClick: () -> Unit,
    ) {
        when (pageState) {
            AddProviderViewModel.PageState.INITIALIZE -> InitializePage(onSearchClick = onSearchClick)
            AddProviderViewModel.PageState.SOURCE_INFO -> BlogSourceInstancePage(
                statusSourceMaintainer = statusSourceMaintainer!!,
                onAddSourceClick = onAddSourceClick,
                onConfirmClick = onConfirmClick
            )
        }
    }

    @Composable
    fun InitializePage(onSearchClick: (String) -> Unit) {
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
                    onClick = { onSearchClick(inputtedContent) }
                ) {
                    Text(text = LocalContext.current.getString(R.string.add))
                }
            }
        }
    }

    @Composable
    fun BlogSourceInstancePage(
        statusSourceMaintainer: StatusSourceMaintainer,
        onAddSourceClick: (source: StatusSource) -> Unit,
        onConfirmClick: () -> Unit,
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
                        model = statusSourceMaintainer.thumbnail,
                        contentDescription = "cover"
                    )

                    Text(
                        modifier = Modifier.padding(top = 10.dp),
                        text = statusSourceMaintainer.url,
                        fontSize = 12.sp
                    )

                    Text(
                        modifier = Modifier.padding(top = 10.dp),
                        text = statusSourceMaintainer.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )

                    Text(
                        modifier = Modifier.padding(top = 10.dp),
                        text = statusSourceMaintainer.description.orEmpty(),
                        fontSize = 14.sp
                    )

                    Column(modifier = Modifier.padding(top = 10.dp)) {
                        statusSourceMaintainer.sourceList.forEach { blogSource ->
                            Surface(
                                modifier = Modifier
                                    .padding(start = 15.dp, end = 15.dp, bottom = 8.dp)
                                    .fillMaxWidth(),
                                elevation = 5.dp,
                            ) {
                                Row(modifier = Modifier.fillMaxSize()) {
                                    Text(
                                        modifier = Modifier
                                            .padding(start = 10.dp)
                                            .align(Alignment.CenterVertically),
                                        text = blogSource.nickName
                                    )
                                    Spacer(modifier = Modifier.weight(1F))
                                    Image(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clickable {
                                                onAddSourceClick(blogSource)
                                            },
                                        imageVector = Icons.Outlined.Add,
                                        contentDescription = "Add server"
                                    )
                                }
                            }
                        }
                    }

                    Button(
                        modifier = Modifier.padding(top = 15.dp),
                        onClick = onConfirmClick
                    ) {
                        Text(text = LocalContext.current.getString(R.string.add_provider_page_confirm_button))
                    }
                }
            }
        }
    }
}