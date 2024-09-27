package com.zhangke.fread.di

import com.zhangke.fread.common.di.ActivityScope
import me.tatarka.inject.annotations.Component

@Component
@ActivityScope
abstract class IosActivityComponent(
    @Component val applicationComponent: IosApplicationComponent,
) : HostingActivityComponent {

    companion object
}
