package com.zhangke.utopia

import androidx.multidex.MultiDexApplication
import com.zhangke.framework.utils.initApplication

/**
 * Created by ZhangKe on 2022/11/27.
 */
class UtopiaApplication : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        initApplication(this)
    }
}