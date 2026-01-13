package com.zhangke.fread

import android.app.Application
import com.seiko.imageloader.ImageLoader
import com.seiko.imageloader.ImageLoaderFactory
import com.zhangke.framework.activity.TopActivityManager
import com.zhangke.framework.utils.initApplication
import com.zhangke.framework.utils.initDebuggable
import com.zhangke.framework.utils.isDebugMode
import com.zhangke.fread.di.FreadApplication
import org.koin.core.component.KoinComponent

abstract class HostingApplication : Application(),
    ImageLoaderFactory,
    KoinComponent {

    override fun onCreate() {
        super.onCreate()
        initDebuggable(isDebugMode())
        initApplication(this)
        TopActivityManager.init(this)

        FreadApplication.initialize()
    }

    override fun newImageLoader(): ImageLoader {
        return getKoin().get()
    }
}