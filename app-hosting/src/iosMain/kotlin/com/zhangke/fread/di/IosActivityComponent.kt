package com.zhangke.fread.di

import com.zhangke.fread.common.di.ActivityScope
import com.zhangke.fread.screen.FreadViewController
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides
import platform.UIKit.UIViewController

@Component
@ActivityScope
abstract class IosActivityComponent(
    @Component val applicationComponent: IosApplicationComponent,
) : HostingActivityComponent {

    abstract val uiViewControllerFactory: () -> UIViewController

    @ActivityScope
    @Provides
    fun uiViewController(bind: FreadViewController): UIViewController = bind()

    companion object
}
