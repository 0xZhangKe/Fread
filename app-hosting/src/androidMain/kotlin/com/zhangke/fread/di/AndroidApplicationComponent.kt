package com.zhangke.fread.di

import android.app.Application
import android.content.Context
import com.seiko.imageloader.ImageLoader
import com.seiko.imageloader.cache.memory.maxSizePercent
import com.seiko.imageloader.component.setupDefaultComponents
import com.seiko.imageloader.intercept.bitmapMemoryCacheConfig
import com.seiko.imageloader.intercept.imageMemoryCacheConfig
import com.seiko.imageloader.intercept.painterMemoryCacheConfig
import com.seiko.imageloader.option.androidContext
import com.zhangke.fread.activitypub.app.di.ActivityPubComponentProvider
import com.zhangke.fread.common.CommonComponentProvider
import com.zhangke.fread.common.di.ApplicationContext
import com.zhangke.fread.common.di.ApplicationScope
import com.zhangke.fread.common.utils.StorageHelper
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides


@Component
@ApplicationScope
abstract class AndroidApplicationComponent(
    @get:Provides val application: Application,
) : HostingApplicationComponent {
    @Provides
    fun provideApplicationContext(): ApplicationContext {
        return application
    }

    @ApplicationScope
    @Provides
    fun provideImageLoader(storageHelper: StorageHelper): ImageLoader {
        return ImageLoader {
            options {
                androidContext(application)
            }
            components {
                setupDefaultComponents()
            }
            interceptor {
                // cache 25% memory bitmap
                bitmapMemoryCacheConfig {
                    maxSizePercent(application, 0.25)
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

    companion object
}

interface ApplicationComponentProvider :
    CommonComponentProvider,
    ActivityPubComponentProvider {
    override val component: AndroidApplicationComponent
}

val Context.component get() = (applicationContext as ApplicationComponentProvider).component
