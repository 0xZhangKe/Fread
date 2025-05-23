package com.zhangke.fread.bluesky.internal.usecase

import com.zhangke.framework.utils.RegexFactory
import com.zhangke.framework.utils.WebFinger
import me.tatarka.inject.annotations.Inject
import sh.christian.ozone.api.AtIdentifier
import sh.christian.ozone.api.Did
import sh.christian.ozone.api.Handle

class GetAtIdentifierUseCase @Inject constructor() {

    operator fun invoke(text: String): AtIdentifier? {
        // did, handle, homePageUrl
        if (RegexFactory.didRegex.matches(text)) return Did(text)
        val webFinger = WebFinger.create(text)
        if (webFinger != null) {
            val handle = webFinger.toString().let { runCatching { Handle(it) } }.getOrNull()
            if (handle != null) return handle
        }
        return text.substringAfterLast("/")
            .let { WebFinger.create(it) }
            ?.let { runCatching { Handle(it.toString()) } }
            ?.getOrNull()
    }
}
