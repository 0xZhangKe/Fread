package com.zhangke.fread.activitypub.app.internal.screen.filters.edit

import androidx.compose.runtime.Composable
import com.zhangke.framework.utils.Parcelize
import com.zhangke.framework.utils.PlatformParcelable
import com.zhangke.framework.utils.PlatformSerializable
import com.zhangke.fread.activitypub.app.Res
import com.zhangke.fread.activitypub.app.activity_pub_filter_edit_duration_finish_subtitle
import com.zhangke.fread.activitypub.app.activity_pub_filter_edit_duration_permanent
import com.zhangke.fread.common.utils.formatDefault
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource

data class EditFilterUiState(
    val title: String,
    val expiresDate: Instant?,
    val keywordList: List<Keyword>,
    val contextList: List<FilterContext>,
    val filterByWarn: Boolean,
    val hasInputtedSomething: Boolean,
) {

    val keywordCount: Int
        get() = keywordList.filter { !it.deleted }.size

    private val expiresDateString: String by lazy {
        if (expiresDate == null) return@lazy ""
        // val dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault())
        // val timeFormat = DateFormat.getTimeInstance(DateFormat.MEDIUM, Locale.getDefault())
        // dateFormat.format(expiresDate) + " " + timeFormat.format(expiresDate)
        expiresDate.formatDefault()
    }

    @Composable
    fun getExpiresDateDesc(): String {
        if (expiresDate == null) {
            return stringResource(Res.string.activity_pub_filter_edit_duration_permanent)
        }
        return stringResource(
            Res.string.activity_pub_filter_edit_duration_finish_subtitle,
            expiresDateString,
        )
    }

    @Parcelize
    @Serializable
    data class Keyword(
        val keyword: String,
        val id: String? = null,
        val deleted: Boolean = false,
        val wholeWord: Boolean = true,
    ) : PlatformSerializable, PlatformParcelable

    companion object {

        fun default(): EditFilterUiState {
            return EditFilterUiState(
                title = "",
                expiresDate = null,
                keywordList = emptyList(),
                contextList = FilterContext.entries,
                filterByWarn = true,
                hasInputtedSomething = false,
            )
        }
    }
}
