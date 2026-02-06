@file:OptIn(ExperimentalMaterial3Api::class)

package com.zhangke.fread.demo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.TabPosition
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.UiComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import com.zhangke.framework.composable.NavigationBar
import com.zhangke.framework.composable.NavigationBarItem
import kotlinx.serialization.Serializable
import kotlin.math.roundToInt

@Serializable
object AppBarScreenNavKey : NavKey

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBarScreen() {
    val items = remember {
        List(100) { index -> "Mock Item #${index + 1}" }
    }
    val tabs = remember {
        listOf("For You", "Following", "Tech", "Design", "Music")
    }
    var selectedIndex by rememberSaveable { mutableStateOf(0) }
    var selectedTabIndex by rememberSaveable { mutableStateOf(0) }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val baseColor = MaterialTheme.colorScheme.surface
    val scrolledColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
    val containerColor by remember {
        derivedStateOf {
            val fraction = scrollBehavior.state.overlappedFraction.coerceIn(0F, 1F)
            lerp(baseColor, scrolledColor, fraction)
        }
    }
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TabsTopAppBarOld(
                title = { Text(text = "AppBar Demo") },
                actions = {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = "Search",
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = "Profile",
                    )
                },
                tabs = tabs,
                selectedTabIndex = selectedTabIndex,
                onTabSelected = { selectedTabIndex = it },
                scrollBehavior = scrollBehavior,
                containerColor = containerColor,
            )
        },
        bottomBar = {
            BottomNavbar(
                selectedIndex = selectedIndex,
                onSelected = { selectedIndex = it },
            )
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = paddingValues.calculateTopPadding() + 8.dp,
                bottom = paddingValues.calculateBottomPadding() + 8.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(items) { title ->
                AppBarCardItem(title = title)
            }
        }
    }
}

@Composable
private fun TabsTopAppBar(
    modifier: Modifier,
    title: @Composable () -> Unit,
    actions: @Composable RowScope.() -> Unit,
    selectedTabIndex: Int,
    scrollBehavior: TopAppBarScrollBehavior,
    edgePadding: Dp = TabRowDefaults.ScrollableTabRowPadding,
    indicator: @Composable @UiComposable (tabPositions: List<TabPosition>) -> Unit =
        @Composable { tabPositions ->
            TabRowDefaults.Indicator(Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]))
        },
    divider: @Composable @UiComposable () -> Unit = @Composable { TabRowDefaults.Divider() },
    tabs: @Composable @UiComposable () -> Unit,
    colors: TabsTopAppBarColors = TabsTopAppBarColors.default(),
) {
    SubcomposeLayout(modifier = modifier) { constraints ->
        val topAppBarPlaceable = subcompose("topAppBar") {
            TopAppBar(
                title = title,
                actions = actions,
            )
        }.first().measure(constraints)
        val tabTowPlaceable = subcompose("tabRow") {
            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                edgePadding = edgePadding,
                indicator = indicator,
                divider = divider,
                tabs = tabs,
            )
        }.first().measure(constraints)
        layout(constraints.maxWidth, constraints.maxHeight) {
            tabTowPlaceable.placeRelative(0, 0)
            topAppBarPlaceable.placeRelative(0, 0)
        }
    }
}

data class TabsTopAppBarColors(
    val containerColor: Color,
    val scrolledContainerColor: Color,
    val contentColor: Color,
) {

    companion object {

        @Composable
        fun default(
            containerColor: Color = MaterialTheme.colorScheme.surface,
            scrolledContainerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
            contentColor: Color = MaterialTheme.colorScheme.onSurface,
        ): TabsTopAppBarColors {
            return TabsTopAppBarColors(
                containerColor = containerColor,
                scrolledContainerColor = scrolledContainerColor,
                contentColor = contentColor,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TabsTopAppBarOld(
    title: @Composable () -> Unit,
    actions: @Composable RowScope.() -> Unit,
    tabs: List<String>,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    containerColor: Color,
    modifier: Modifier = Modifier,
    topBarHeight: Dp = 64.dp,
    tabRowHeight: Dp = 48.dp,
) {
    val density = LocalDensity.current
    SideEffect {
        val totalHeightPx = with(density) { (topBarHeight + tabRowHeight).toPx() }
        scrollBehavior.state.heightOffsetLimit = -totalHeightPx
    }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(containerColor)
            .offset {
                IntOffset(0, scrollBehavior.state.heightOffset.roundToInt())
            },
    ) {
        TopAppBar(
            title = title,
            actions = actions,
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = containerColor,
                scrolledContainerColor = containerColor,
            ),
            scrollBehavior = scrollBehavior,
        )
        ScrollableTabRow(
            selectedTabIndex = selectedTabIndex,
            backgroundColor = containerColor,
        ) {
            tabs.forEachIndexed { index, text ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { onTabSelected(index) },
                    text = { Text(text = text) },
                )
            }
        }
    }
}

@Composable
private fun BottomNavbar(
    selectedIndex: Int,
    onSelected: (Int) -> Unit,
) {
    NavigationBar {
        NavigationBarItem(
            selected = selectedIndex == 0,
            onClick = { onSelected(0) },
            icon = { Icon(imageVector = Icons.Outlined.Home, contentDescription = "Home") },
            label = { Text(text = "Home") },
        )
        NavigationBarItem(
            selected = selectedIndex == 1,
            onClick = { onSelected(1) },
            icon = { Icon(imageVector = Icons.Outlined.Search, contentDescription = "Search") },
            label = { Text(text = "Search") },
        )
        NavigationBarItem(
            selected = selectedIndex == 2,
            onClick = { onSelected(2) },
            icon = { Icon(imageVector = Icons.Outlined.Person, contentDescription = "Profile") },
            label = { Text(text = "Profile") },
        )
    }
}

@Composable
private fun AppBarCardItem(title: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .background(Color.Blue.copy(alpha = 0.6F))
                    .size(56.dp),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Image,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp),
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = "A short description for $title",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
