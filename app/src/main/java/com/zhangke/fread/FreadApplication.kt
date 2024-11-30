package com.zhangke.fread

import android.app.Application
import com.seiko.imageloader.ImageLoader
import com.seiko.imageloader.ImageLoaderFactory
import com.seiko.imageloader.cache.memory.maxSizePercent
import com.seiko.imageloader.component.setupDefaultComponents
import com.seiko.imageloader.intercept.bitmapMemoryCacheConfig
import com.seiko.imageloader.intercept.imageMemoryCacheConfig
import com.seiko.imageloader.intercept.painterMemoryCacheConfig
import com.seiko.imageloader.option.androidContext
import com.zhangke.framework.activity.TopActivityManager
import com.zhangke.framework.utils.initApplication
import com.zhangke.framework.utils.initDebuggable
import com.zhangke.fread.di.ApplicationComponent
import com.zhangke.fread.di.ApplicationComponentProvider
import com.zhangke.fread.di.create
import okio.Path.Companion.toOkioPath

/**
 * Created by ZhangKe on 2022/11/27.
 */
class FreadApplication : Application(), ApplicationComponentProvider, ImageLoaderFactory {

    override val component: ApplicationComponent by lazy(LazyThreadSafetyMode.NONE) {
        ApplicationComponent.create(this)
    }

    override fun onCreate() {
        super.onCreate()
        initDebuggable(BuildConfig.DEBUG)
        initApplication(this)
        initModuleStartups()
        TopActivityManager.init(this)
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader {
            options {
                androidContext(applicationContext)
            }
            components {
                setupDefaultComponents()
            }
            interceptor {
                // cache 25% memory bitmap
                bitmapMemoryCacheConfig {
                    maxSizePercent(applicationContext, 0.25)
                }
                // cache 50 image
                imageMemoryCacheConfig {
                    maxSize(50)
                }
                // cache 50 painter
                painterMemoryCacheConfig {
                    maxSize(50)
                }
                diskCacheConfig {
                    directory(cacheDir.resolve("image_cache").toOkioPath())
                    maxSizeBytes(512L * 1024 * 1024) // 512MB
                }
            }
        }
    }

    private fun initModuleStartups() {
        component.moduleStartups.forEach {
            it.onAppCreate()
        }
    }
}
