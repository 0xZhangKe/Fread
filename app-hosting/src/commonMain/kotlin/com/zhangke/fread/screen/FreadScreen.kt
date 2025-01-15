package com.zhangke.fread.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.utils.PlatformSerializable
import com.zhangke.fread.common.page.BaseScreen
import kotlinx.serialization.Serializable

interface Html {

    val document: String
}

@Serializable
class Html1 : Html, PlatformSerializable {

    override val document: String
        get() = "html1"
}

class FreadScreen : BaseScreen() {

    @Composable
    override fun Content() {
        super.Content()
//        MainPage()
        Box(
            modifier = Modifier.fillMaxSize()
        ){
            val navigator = LocalNavigator.currentOrThrow
            Button(
                onClick = {
                    navigator.push(HtmlScreen(Html1()))
                }
            ){
                Text("GO")
            }
        }
    }
}

class HtmlScreen(val html: Html) : BaseScreen() {

    @Composable
    override fun Content() {
        super.Content()
        val navigator = LocalNavigator.currentOrThrow
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {

            Text(
                text = html.document,
            )

            Button(
                onClick = {
                    navigator.push(SecondScreen())
                },
            ) {
                Text("Go")
            }
        }
    }
}

class SecondScreen() : BaseScreen() {

    @Composable
    override fun Content() {
        super.Content()
        Box(modifier = Modifier.fillMaxSize()) {
            Text(
                text = "Second Screen",
                modifier = Modifier.align(Alignment.Center),
            )
        }
    }
}