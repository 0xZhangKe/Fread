package com.zhangke.utopia

import android.content.Context
import android.os.Build.VERSION.SDK_INT
import android.util.Log
import androidx.multidex.MultiDexApplication
import coil.Coil
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.zhangke.framework.utils.initApplication
import com.zhangke.framework.utils.initDebuggable
import dagger.hilt.android.HiltAndroidApp
import java.util.Locale

/**
 * Created by ZhangKe on 2022/11/27.
 */
@HiltAndroidApp
class UtopiaApplication : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        initDebuggable(BuildConfig.DEBUG)
        initApplication(this)
        initCoil(this)

        val locales = Locale.getAvailableLocales()

        locales.distinctBy { it.language to it.getDisplayLanguage(Locale.ENGLISH) }
            .forEach {
                Log.d("U_TEST", it.getDisplayName(it))
            }
//        val languageList = locales.map { locale ->
//            locale.language to locale.getDisplayLanguage(Locale.ENGLISH)
//        }.distinct().sortedBy { it.second }
//
//        // 遍历并打印所有语言名称及其编码
//        for ((isoCode, displayName) in languageList) {
//            Log.d("U_TEST", "$isoCode -> $displayName")
//        }

//
//        Locale.getISOCountries().forEach {
//
//        }
//        Locale.getISOLanguages()
//            .filter { it.length == 2 }
//            .map { Locale(it) }
//            .forEach {
//                Log.d("U_TEST", it.getDisplayName(it))
//            }

//        Log.d("U_TEST", Locale.ENGLISH.getDisplayName(Locale.ENGLISH))
//        Log.d("U_TEST", Locale.FRANCE.getDisplayName(Locale.FRANCE))
//        Log.d("U_TEST", Locale.ITALIAN.getDisplayName(Locale.ITALIAN))
//        Log.d("U_TEST", Locale.TAIWAN.getDisplayName(Locale.TAIWAN))
//        Log.d("U_TEST", Locale.CHINESE.getDisplayName(Locale.CHINESE))
//        Log.d("U_TEST", Locale.getISOLanguages().joinToString(","))
//        Locale.getISOLanguages()
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