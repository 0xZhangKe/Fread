package com.zhangke.utopia.common.status.usecase

import com.zhangke.framework.collections.mapFirstOrNull
import com.zhangke.utopia.common.status.model.StatusUiInteraction
import com.zhangke.utopia.status.status.model.StatusInteraction
import javax.inject.Inject

class GenerateMoreInteraction @Inject constructor() {

    operator fun invoke(interactions: List<StatusInteraction>): List<StatusUiInteraction> {
        val moreInteractions = mutableListOf<StatusUiInteraction>()
        interactions.mapFirstOrNull { it as? StatusInteraction.Bookmark }?.let {
            if (it.enable) {
                moreInteractions += StatusUiInteraction.Bookmark(it)
            }
        }
        interactions.mapFirstOrNull { it as? StatusInteraction.Delete }?.let {
            if (it.enable) {
                moreInteractions += StatusUiInteraction.Delete(it)
            }
        }
        return moreInteractions
    }
}
