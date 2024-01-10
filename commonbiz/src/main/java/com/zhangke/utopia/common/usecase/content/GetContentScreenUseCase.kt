package com.zhangke.utopia.common.usecase.content

import com.zhangke.utopia.status.StatusProvider
import com.zhangke.utopia.status.model.ContentConfig
import javax.inject.Inject

class GetContentScreenUseCase @Inject constructor(
    private val statusProvider: StatusProvider,
) {

    operator fun invoke(contentConfig: ContentConfig): Any? {
        if (contentConfig is ContentConfig.MixedContent){

        }
        return statusProvider.screenProvider.getContentScreen(contentConfig)
    }
}
