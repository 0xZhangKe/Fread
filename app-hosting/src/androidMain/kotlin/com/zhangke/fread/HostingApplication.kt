package com.zhangke.fread

import android.app.Application
import com.seiko.imageloader.ImageLoader
import com.seiko.imageloader.ImageLoaderFactory
import com.zhangke.framework.activity.TopActivityManager
import com.zhangke.framework.utils.initApplication
import com.zhangke.framework.utils.initDebuggable
import com.zhangke.framework.utils.isDebugMode
import com.zhangke.fread.common.commonComponentProvider
import com.zhangke.fread.di.AndroidApplicationComponent
import com.zhangke.fread.di.ApplicationComponentProvider
import com.zhangke.fread.di.FreadApplication
import com.zhangke.fread.di.create

abstract class HostingApplication : Application(),
    ApplicationComponentProvider,
    ImageLoaderFactory {

    override val component: AndroidApplicationComponent by lazy(LazyThreadSafetyMode.NONE) {
        AndroidApplicationComponent.create(this)
    }

    override fun onCreate() {
        super.onCreate()
        initDebuggable(isDebugMode())
        initApplication(this)
        commonComponentProvider = this
        initModuleStartups()
        TopActivityManager.init(this)
        component.dayNightHelper // Initialize the DayNightHelper to set the default mode

        FreadApplication.initialize()
    }

    override fun newImageLoader(): ImageLoader {
        return component.imageLoader
    }

    private fun initModuleStartups() {
        component.startupManager.initialize()
    }
}