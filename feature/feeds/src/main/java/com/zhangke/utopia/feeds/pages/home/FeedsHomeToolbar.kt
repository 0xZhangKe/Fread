package com.zhangke.utopia.feeds.pages.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zhangke.framework.composable.TextWithIcon
import com.zhangke.framework.composable.UtopiaTabRow
import com.zhangke.framework.composable.theme.TopAppBarDefault
import com.zhangke.utopia.common.status.FeedsConfig
import com.zhangke.utopia.status.platform.BlogPlatform

@Composable
fun FeedsHomeTopBar(
    modifier: Modifier,
    selectedIndex: Int,
    feedsConfigList: List<FeedsConfig>,
    onTabClick: (Int) -> Unit,
    platformList: List<BlogPlatform>,
    onServerItemClick: (BlogPlatform) -> Unit,
    onMenuClick: () -> Unit,
) {
    var showSelectSourcePopup by remember {
        mutableStateOf(false)
    }
    Column(modifier = modifier) {
        Column {
            Row(
                modifier = Modifier
                    .padding(
                        start = TopAppBarDefault.StartPadding,
                        end = TopAppBarDefault.EndPadding
                    )
                    .height(TopAppBarDefault.TopBarHeight),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TextWithIcon(
                    modifier = Modifier.clickable {
                        if (platformList.size > 1) {
                            showSelectSourcePopup = true
                        } else {
                            onServerItemClick(platformList.first())
                        }
                    },
                    text = platformList.firstOrNull()?.name.orEmpty(),
                    fontSize = 18.sp,
                    endIcon = {
                        if (platformList.size > 1) {
                            Icon(
                                modifier = Modifier.padding(start = 4.dp),
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "",
                            )
                        }
                    }
                )
                Box(modifier = Modifier.weight(1F))
                IconButton(onClick = onMenuClick) {
                    Icon(
                        painter = rememberVectorPainter(Icons.Default.Menu),
                        contentDescription = "Menu",
                    )
                }
            }

            DropdownMenu(
                offset = DpOffset(x = 30.dp, y = 8.dp),
                expanded = showSelectSourcePopup,
                onDismissRequest = { showSelectSourcePopup = false },
            ) {
                platformList.forEach { source ->
                    DropdownMenuItem(
                        onClick = {
                            showSelectSourcePopup = false
                            onServerItemClick(source)
                        },
                        text = {
                            Text(text = source.name)
                        }
                    )
                }
            }
        }

        UtopiaTabRow(
            modifier = Modifier.fillMaxWidth(),
            selectedTabIndex = selectedIndex,
            tabCount = feedsConfigList.size,
            containerColor = Color.Transparent,
            selectedIndex = selectedIndex,
            onTabClick = onTabClick,
            tabContent = { index ->
                Box(
                    modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
                ) {
                    Text(
                        text = feedsConfigList[index].name,
                    )
                }
            }
        )
    }
}
