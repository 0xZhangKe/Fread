package com.zhangke.utopia

import android.content.Context
import android.os.Build.VERSION.SDK_INT
import androidx.multidex.MultiDexApplication
import coil.Coil
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.zhangke.framework.utils.initApplication

/**
 * Created by ZhangKe on 2022/11/27.
 */
class UtopiaApplication : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        initApplication(this)
        initCoil(this)
    }

    private fun initCoil(context: Context) {
        val imageLoader = ImageLoader.Builder(context)
            .components {
                if (SDK_INT >= 28) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .build()
        Coil.setImageLoader(imageLoader)
    }
}