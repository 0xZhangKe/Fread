package com.zhangke.utopia.feeds.pages.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.DrawerValue
import androidx.compose.material.ModalDrawer
import androidx.compose.material.rememberDrawerState
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.androidx.AndroidScreen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.utopia.feeds.pages.home.drawer.ContentHomeDrawer
import com.zhangke.utopia.feeds.pages.manager.selecttype.SelectContentTypeScreen

class ContentHomeScreen : AndroidScreen() {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        ModalDrawer(
            drawerState = drawerState,
            drawerContent = {
                ContentHomeDrawer()
            }) {
            Box(modifier = Modifier.fillMaxSize()) {
                Button(
                    onClick = {
                        navigator.push(SelectContentTypeScreen())
                    },
                ) {
                    Text(text = "Add Content")
                }
            }
        }
    }
}
