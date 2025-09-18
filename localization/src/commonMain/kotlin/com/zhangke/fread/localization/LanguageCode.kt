package com.zhangke.fread.localization

enum class LanguageCode(val code: String) {
    EN_US("en-US"),
    DE_DE("de-DE"),
    ES_ES("es-ES"),
    FR_FR("fr-FR"),
    JA_JP("ja-JP"),
    PT_PT("pt-PT"),
    RU_RU("ru-RU"),
    ZH_CN("zh-CN"),
    ZH_HK("zh-HK"),
    ZH_TW("zh-TW");

    companion object {

        fun fromCode(code: String): LanguageCode? {
            return LanguageCode.entries.find { it.code == code }
        }
    }
}
