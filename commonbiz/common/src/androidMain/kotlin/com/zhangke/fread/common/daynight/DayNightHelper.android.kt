package com.zhangke.fread.common.daynight

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.zhangke.fread.common.di.ActivityScope
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

@ActivityScope
actual class ActivityDayNightHelper @Inject constructor(
    private val dayNightHelper: DayNightHelper,
    private val activity: ComponentActivity,
) {

    actual val dayNightModeFlow get() = dayNightHelper.dayNightModeFlow

    fun setDefaultMode() {
        Log.d("F_TEST", "setDefaultMode: ${dayNightModeFlow.value.modeValue}")
        AppCompatDelegate.setDefaultNightMode(dayNightModeFlow.value.modeValue)
    }

    actual fun setMode(mode: DayNightMode) {
        activity.lifecycleScope.launch {
            dayNightHelper.setMode(mode)
            activity.recreate()
        }
    }
}

internal actual fun setDefaultNightMode(modeValue: Int) {
    Log.d("F_TEST", "setDefaultNightMode: $modeValue")
    AppCompatDelegate.setDefaultNightMode(modeValue)
}

internal actual val DayNightMode.modeValue: Int
    get() = when (this) {
        DayNightMode.DAY -> AppCompatDelegate.MODE_NIGHT_NO
        DayNightMode.NIGHT -> AppCompatDelegate.MODE_NIGHT_YES
        DayNightMode.FOLLOW_SYSTEM -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
    }

