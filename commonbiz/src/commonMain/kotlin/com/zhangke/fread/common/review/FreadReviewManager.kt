package com.zhangke.fread.common.review

import androidx.compose.runtime.staticCompositionLocalOf
import com.zhangke.framework.architect.coroutines.ApplicationScope
import com.zhangke.fread.common.config.LocalConfigManager
import com.zhangke.fread.common.di.ApplicationScope
import com.zhangke.fread.common.ext.getCurrentTimeMillis
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.milliseconds

@ApplicationScope
class FreadReviewManager @Inject constructor(
    private val localConfigManager: LocalConfigManager,
) {

    companion object {
        private const val LOCAL_KEY_LATEST_SHOW_TIME = "latestShowPlayReviewTime"
        private const val LOCAL_KEY_REVIEWED = "playReviewed"
        private const val LOCAL_KET_REVIEW_POP_COUNT = "playReviewPopCount"
        private val maxInternal = 90.days
    }

    fun trigger(forceShow: Boolean = false) {
        if (forceShow) {
            showPlayReviewPopup(this)
        } else {
            ApplicationScope.launch {
                maybeShowPlayReviewPopup()
            }
        }
    }

    private suspend fun maybeShowPlayReviewPopup() {
        val reviewed = getReviewed()
        if (reviewed) return
        val count = getPlayReviewPopCount()
        val latestShowTime = getLatestShowPlayReviewTime()
        if (count == 0 && latestShowTime == 0) {
            // mark first launching app time
            setLatestShowPlayReviewTime()
            return
        }
        val duration = getCurrentTimeMillis().milliseconds - latestShowTime.milliseconds
        if (isDurationOvertime(duration, count)) {
            showPlayReviewPopup(this)
        }
    }

    private fun isDurationOvertime(duration: Duration, count: Int): Boolean {
        if (duration > maxInternal) return true
        if (count == 0) return duration >= 3.days
        if (count == 1) return duration > 10.days
        if (count == 2) return duration > 30.days
        return false
    }

    internal fun onReviewSuccess() {
        ApplicationScope.launch {
            setReviewed()
        }
    }

    internal fun onReviewCancel() {
        ApplicationScope.launch {
            increasePlayReviewPopCount()
            setLatestShowPlayReviewTime()
        }
    }

    private suspend fun setReviewed() {
        localConfigManager.putBoolean(LOCAL_KEY_REVIEWED, true)
    }

    private suspend fun getReviewed(): Boolean {
        return localConfigManager.getBoolean(LOCAL_KEY_REVIEWED) ?: false
    }

    private suspend fun increasePlayReviewPopCount() {
        val count = getPlayReviewPopCount() + 1
        localConfigManager.putInt(LOCAL_KET_REVIEW_POP_COUNT, count)
    }

    private suspend fun getPlayReviewPopCount(): Int {
        return localConfigManager.getInt(LOCAL_KET_REVIEW_POP_COUNT) ?: 0
    }

    private suspend fun setLatestShowPlayReviewTime() {
        val time = (getCurrentTimeMillis() / 1000).toInt()
        localConfigManager.putInt(LOCAL_KEY_LATEST_SHOW_TIME, time)
    }

    private suspend fun getLatestShowPlayReviewTime(): Int {
        return localConfigManager.getInt(LOCAL_KEY_LATEST_SHOW_TIME) ?: 0
    }
}

internal expect fun showPlayReviewPopup(freadReviewManager: FreadReviewManager)

val LocalFreadReviewManager = staticCompositionLocalOf<FreadReviewManager> { error("No FreadReviewManager provided") }
