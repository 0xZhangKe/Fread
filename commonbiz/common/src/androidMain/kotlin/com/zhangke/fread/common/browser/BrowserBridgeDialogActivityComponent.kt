package com.zhangke.fread.common.browser

import android.app.Activity
import androidx.activity.ComponentActivity
import com.zhangke.fread.common.di.ActivityScope
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@Component
@ActivityScope
internal abstract class BrowserBridgeDialogActivityComponent(
    @get:Provides val activity: ComponentActivity,
) {

    companion object
}
