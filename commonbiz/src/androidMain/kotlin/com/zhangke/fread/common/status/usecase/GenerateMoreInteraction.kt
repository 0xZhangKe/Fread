package com.zhangke.fread.common.status.usecase

import com.zhangke.fread.common.status.model.StatusUiInteraction
import com.zhangke.fread.status.status.model.StatusInteraction
import me.tatarka.inject.annotations.Inject

class GenerateMoreInteraction @Inject constructor() {

    operator fun invoke(interactions: List<StatusInteraction>): List<StatusUiInteraction> {
        val moreInteractions = mutableListOf<StatusUiInteraction>()
        interactions.firstNotNullOfOrNull { it as? StatusInteraction.Bookmark }?.let {
            if (it.enable) {
                moreInteractions += StatusUiInteraction.Bookmark(it)
            }
        }
        interactions.firstNotNullOfOrNull { it as? StatusInteraction.Delete }?.let {
            if (it.enable) {
                moreInteractions += StatusUiInteraction.Delete(it)
            }
        }
        interactions.firstNotNullOfOrNull { it as? StatusInteraction.Pin }?.let {
            if (it.enable) {
                moreInteractions += StatusUiInteraction.Pin(it)
            }
        }
        return moreInteractions
    }
}
