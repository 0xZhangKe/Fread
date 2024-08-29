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
import com.zhangke.framework.architect.coroutines.ApplicationScope
import com.zhangke.framework.module.ModuleStartup
import com.zhangke.framework.utils.initApplication
import com.zhangke.framework.utils.initDebuggable
import com.zhangke.fread.common.daynight.DayNightHelper
import com.zhangke.fread.common.language.LanguageHelper
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.launch
import okio.Path.Companion.toOkioPath
import javax.inject.Inject

/**
 * Created by ZhangKe on 2022/11/27.
 */
@HiltAndroidApp
class FreadApplication : Application(), ImageLoaderFactory {

    var moduleStartups: Set<@JvmSuppressWildcards ModuleStartup>? = null
        @Inject set

    override fun onCreate() {
        super.onCreate()
        initDebuggable(BuildConfig.DEBUG)
        initApplication(this)
        DayNightHelper
        LanguageHelper.prepare(this)
        initModuleStartups()
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
        ApplicationScope.launch {
            moduleStartups?.forEach {
                it.onAppCreate(this@FreadApplication)
            }
        }
    }
}
