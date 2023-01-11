package com.zhangke.utopia.pages.providermanager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.TextField
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
import com.zhangke.framework.architect.theme.UtopiaTheme
import com.zhangke.utopia.R
import com.zhangke.utopia.blogprovider.BlogSource

class AddProviderFragment : Fragment() {

    companion object {

        fun newInstance(): AddProviderFragment {
            return AddProviderFragment()
        }
    }

    private val viewModel: AddProviderViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher
            .addCallback(this) {
                onNavigationBackClick()
            }
    }

    private fun onNavigationBackClick() {
        if (viewModel.pageState.value != AddProviderViewModel.PageState.INITIALIZE) {
            viewModel.moveToInitializedPage()
        } else {
            requireActivity().onBackPressedDispatcher.onBackPressed()
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
                    val blogSource =
                        if (pageState == AddProviderViewModel.PageState.SOURCE_INFO) {
                            viewModel.requireActivityPubInstance()
                        } else {
                            null
                        }
                    AddProviderPage(
                        pageState = pageState,
                        blogSource = blogSource,
                        onAddClick = {
                            viewModel.onAddClick(it)
                        },
                        onConfirmAddClick = {

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
            onAddClick = {},
            onConfirmAddClick = {}
        )
    }

    @Preview
    @Composable
    fun PreviewActivityPubPage() {
        val blogSource = BlogSource(
            sourceServer = "musician.social",
            protocol = "activity_pub",
            sourceName = "musician",
            sourceDescription = "MUSICIAN.SOCIAL is an server focused on Musicians who create, play, or love #jazz, #rock, #pop, #indie, #classical and all other types of #music",
            avatar = "https://proxy.joinmastodon.org/7c9597cc47c440d758735af7c019d1d0c4189763/68747470733a2f2f66656469686f73742d6d376e2d6d7573696369616e2d6173736574732e73332e75732d776573742d322e616d617a6f6e6177732e636f6d2f66656469686f73742d6d376e2d6d7573696369616e2d6173736574732f736974655f75706c6f6164732f66696c65732f3030302f3030302f3030312f4031782f376364393863306634326331636438632e706e67",
        )
        AddProviderPage(
            pageState = AddProviderViewModel.PageState.SOURCE_INFO,
            blogSource = blogSource,
            onAddClick = {},
            onConfirmAddClick = {}
        )
    }

    @Composable
    fun AddProviderPage(
        pageState: AddProviderViewModel.PageState,
        blogSource: BlogSource?,
        onAddClick: (String) -> Unit,
        onConfirmAddClick: () -> Unit,
    ) {
        when (pageState) {
            AddProviderViewModel.PageState.INITIALIZE -> InitializePage(onAddClick = onAddClick)
            AddProviderViewModel.PageState.SOURCE_INFO -> BlogSourceInstancePage(
                blogSource = blogSource!!,
                onConfirmAddClick = onConfirmAddClick
            )
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
    fun BlogSourceInstancePage(
        blogSource: BlogSource,
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
                        model = blogSource.avatar,
                        contentDescription = "cover"
                    )

                    Text(
                        modifier = Modifier.padding(top = 10.dp),
                        text = blogSource.sourceServer,
                        fontSize = 12.sp
                    )

                    Text(
                        modifier = Modifier.padding(top = 10.dp),
                        text = blogSource.sourceName.orEmpty(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )

                    Text(
                        modifier = Modifier.padding(top = 10.dp),
                        text = blogSource.sourceDescription.orEmpty(),
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