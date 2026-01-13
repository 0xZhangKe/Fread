package com.zhangke.fread.di

import com.seiko.imageloader.ImageLoader
import com.seiko.imageloader.cache.memory.maxSizePercent
import com.seiko.imageloader.component.setupDefaultComponents
import com.seiko.imageloader.intercept.bitmapMemoryCacheConfig
import com.seiko.imageloader.intercept.imageMemoryCacheConfig
import com.seiko.imageloader.intercept.painterMemoryCacheConfig
import com.seiko.imageloader.option.androidContext
import com.zhangke.fread.common.utils.StorageHelper
import com.zhangke.fread.utils.ActivityHelper
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf

actual fun Module.createPlatformModule() {
    singleOf(::ActivityHelper)
    single<ImageLoader> {
        val context = androidContext()
        val storageHelper = get<StorageHelper>()
        ImageLoader {
            options {
                androidContext(context)
            }
            components {
                setupDefaultComponents()
            }
            interceptor {
                // cache 25% memory bitmap
                bitmapMemoryCacheConfig {
                    maxSizePercent(context, 0.25)
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
                    directory(storageHelper.cacheDir.resolve("image_cache"))
                    maxSizeBytes(512L * 1024 * 1024) // 512MB
                }
            }
        }
    }
}
