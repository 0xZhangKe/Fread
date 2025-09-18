package com.zhangke.fread.common.language

import androidx.compose.runtime.Composable
import com.zhangke.fread.localization.LanguageCode
import com.zhangke.fread.localization.LocalizedString
import com.zhangke.fread.localization.displayName
import org.jetbrains.compose.resources.stringResource

sealed interface LanguageSettingItem {

    companion object {

        val items: List<LanguageSettingItem>
            get() = buildList {
                add(FollowSystem)
                LanguageCode.entries.forEach {
                    add(Language(it))
                }
            }

        fun fromLocalId(id: String): LanguageSettingItem? {
            return if (id == FollowSystem.LOCAL_ID) {
                FollowSystem
            } else {
                LanguageCode.fromCode(id)?.let { Language(it) }
            }
        }
    }

    val localId: String

    @Composable
    fun getDisplayName(): String

    data object FollowSystem : LanguageSettingItem {

        const val LOCAL_ID = "FOLLOW_SYSTEM"

        override val localId: String = LOCAL_ID

        @Composable
        override fun getDisplayName(): String {
            return stringResource(LocalizedString.profileSettingLanguageSystem)
        }
    }

    data class Language(val code: LanguageCode) : LanguageSettingItem {

        override val localId: String = code.code

        @Composable
        override fun getDisplayName(): String {
            return code.displayName
        }
    }
}