package com.zhangke.fread.di

import com.seiko.imageloader.ImageLoader
import com.seiko.imageloader.component.setupDefaultComponents
import com.seiko.imageloader.intercept.bitmapMemoryCacheConfig
import com.seiko.imageloader.intercept.imageMemoryCacheConfig
import com.seiko.imageloader.intercept.painterMemoryCacheConfig
import com.zhangke.framework.module.ModuleStartup
import com.zhangke.fread.common.utils.StorageHelper
import com.zhangke.fread.startup.KRouterStartup
import com.zhangke.fread.utils.ActivityHelper
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import platform.UIKit.UIApplication

actual fun Module.createPlatformModule() {
    singleOf(::ActivityHelper)
    single<ImageLoader> {
        ImageLoader {
            components {
                setupDefaultComponents()
            }
            interceptor {
                // cache 32MB bitmap
                bitmapMemoryCacheConfig {
                    maxSize(32 * 1024 * 1024) // 32MB
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
                    directory(get<StorageHelper>().cacheDir.resolve("image_cache"))
                    maxSizeBytes(512L * 1024 * 1024) // 512MB
                }
            }
        }
    }
    factory<UIApplication> { UIApplication.sharedApplication }
    factory<ModuleStartup> { KRouterStartup() }
}
