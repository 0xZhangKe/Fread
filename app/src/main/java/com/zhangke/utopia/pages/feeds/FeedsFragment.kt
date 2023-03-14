package com.zhangke.utopia.pages.feeds

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.zhangke.framework.architect.theme.UtopiaTheme
import com.zhangke.utopia.status_provider.Status
import com.zhangke.utopia.composable.UtopiaStatusComposable

class FeedsFragment : Fragment() {

    companion object {

        private const val ARG_FEEDS_ID = "arg_feeds_id"

        fun newInstance(id: Int): FeedsFragment {
            return FeedsFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_FEEDS_ID, id)
                }
            }
        }
    }

    private val viewModel: FeedsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel.feedsId = requireArguments().getInt(ARG_FEEDS_ID)
        return ComposeView(requireContext()).apply {
            setContent {
                UtopiaTheme {
                    val feeds = viewModel.feeds.collectAsState(initial = emptyList()).value
                    FeedPage(feeds)
                }
            }
        }
    }

    @Composable
    fun FeedPage(status: List<Status>) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 10.dp, horizontal = 15.dp),
            content = {
                items(status) { item ->
                    UtopiaStatusComposable(
                        modifier = Modifier.padding(bottom = 15.dp),
                        status = item
                    )
                }
            })
    }
}