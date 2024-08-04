package com.zhangke.fread

import android.app.Application
import android.content.Context
import android.os.Build.VERSION.SDK_INT
import coil.Coil
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.decode.VideoFrameDecoder
import com.zhangke.framework.architect.coroutines.ApplicationScope
import com.zhangke.framework.module.ModuleStartup
import com.zhangke.framework.utils.initApplication
import com.zhangke.framework.utils.initDebuggable
import com.zhangke.fread.common.daynight.DayNightHelper
import com.zhangke.fread.common.language.LanguageHelper
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by ZhangKe on 2022/11/27.
 */
@HiltAndroidApp
class FreadApplication : Application() {

    var moduleStartups: Set<@JvmSuppressWildcards ModuleStartup>? = null
        @Inject set

    override fun onCreate() {
        super.onCreate()
        initDebuggable(BuildConfig.DEBUG)
        initApplication(this)
        DayNightHelper
        LanguageHelper.prepare(this)
        initCoil(this)
        initModuleStartups()
    }

    private fun initCoil(context: Context) {
        val imageLoader = ImageLoader.Builder(context)
            .components {
                if (SDK_INT >= 28) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
                add(VideoFrameDecoder.Factory())
            }
            .build()
        Coil.setImageLoader(imageLoader)
    }

    private fun initModuleStartups() {
        ApplicationScope.launch {
            moduleStartups?.forEach {
                it.onAppCreate(this@FreadApplication)
            }
        }
    }
}
