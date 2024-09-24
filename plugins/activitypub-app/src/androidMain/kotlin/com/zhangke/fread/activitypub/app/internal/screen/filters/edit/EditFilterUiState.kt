package com.zhangke.fread.activitypub.app.internal.screen.filters.edit

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.zhangke.fread.activitypub.app.R
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import java.text.DateFormat
import java.util.Date
import java.util.Locale

data class EditFilterUiState(
    val title: String,
    val expiresDate: Date?,
    val keywordList: List<Keyword>,
    val contextList: List<FilterContext>,
    val filterByWarn: Boolean,
    val hasInputtedSomething: Boolean,
) {

    val keywordCount: Int
        get() = keywordList.filter { !it.deleted }.size

    private val expiresDateString: String by lazy {
        if (expiresDate == null) return@lazy ""
        val dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault())
        val timeFormat = DateFormat.getTimeInstance(DateFormat.MEDIUM, Locale.getDefault())
        dateFormat.format(expiresDate) + " " + timeFormat.format(expiresDate)
    }

    @Composable
    fun getExpiresDateDesc(): String {
        if (expiresDate == null) {
            return stringResource(id = R.string.activity_pub_filter_edit_duration_permanent)
        }
        return stringResource(
            id = R.string.activity_pub_filter_edit_duration_finish_subtitle,
            expiresDateString,
        )
    }

    @Parcelize
    @Serializable
    data class Keyword(
        val keyword: String,
        val id: String? = null,
        val deleted: Boolean = false,
    ) : java.io.Serializable, Parcelable

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
