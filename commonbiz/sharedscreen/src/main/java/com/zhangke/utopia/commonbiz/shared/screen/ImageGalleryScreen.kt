package com.zhangke.utopia.commonbiz.shared.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.zhangke.framework.voyager.TransparentAndroidScreen
import com.zhangke.krouter.Destination
import com.zhangke.utopia.commonbiz.shared.router.SharedRouter

@Destination(SharedRouter.Common.imageGallery)
class ImageGalleryScreen : TransparentAndroidScreen() {

    @Composable
    override fun Content() {

        Box(modifier = Modifier.fillMaxSize()) {
            Button(
                modifier = Modifier.align(Alignment.Center),
                onClick = { /*TODO*/ }) {
                Text(text = "ssss")
            }
        }
    }
}
