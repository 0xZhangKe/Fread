package com.zhangke.utopia.profile.screen.setting

import android.content.Context
import android.content.pm.PackageManager
import cafe.adriel.voyager.core.model.ScreenModel
import com.zhangke.framework.ktx.launchInScreenModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class SettingScreenModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
): ScreenModel {

    private val _uiState = MutableStateFlow(SettingUiState(getAppVersionInfo()))
    val uiState = _uiState.asStateFlow()

    init {
        launchInScreenModel {

        }
    }

    fun onChangeDayNightMode(){

    }

    private fun getAppVersionInfo(): String{
        return try {
            val info = appContext.packageManager.getPackageInfo(appContext.packageName, 0)
            @Suppress("DEPRECATION")
            "${info.versionName}(${info.versionCode})"
        } catch (e: PackageManager.NameNotFoundException) {
            "unknown"
        }
    }
}