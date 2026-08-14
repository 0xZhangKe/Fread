package com.zhangke.fread.status

import com.zhangke.fread.status.richtext.translate.RichTextTranslatorParser
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val statusProviderModel = module {

    single { StatusProvider(getAll<IStatusProvider>()) }

    singleOf(::RichTextTranslatorParser)
}
