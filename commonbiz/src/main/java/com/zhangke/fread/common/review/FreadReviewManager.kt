package com.zhangke.fread.common.review

import android.content.Context
import android.util.Log
import com.google.android.play.core.review.ReviewException
import com.google.android.play.core.review.ReviewManagerFactory
import com.zhangke.framework.activity.TopActivityManager
import com.zhangke.framework.architect.coroutines.ApplicationScope
import com.zhangke.framework.utils.appContext
import com.zhangke.fread.common.config.LocalConfigManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

object FreadReviewManager {

    private const val LOCAL_KEY_LATEST_SHOW_TIME = "latestShowPlayReviewTime"
    private const val LOCAL_KEY_REVIEWED = "playReviewed"
    private const val LOCAL_KET_REVIEW_POP_COUNT = "playReviewPopCount"
    private val maxInternal = 90.days

    fun trigger(forceShow: Boolean = false) {
        if (forceShow) {
            showPlayReviewPopup()
        } else {
            ApplicationScope.launch {
                maybeShowPlayReviewPopup()
            }
        }
    }

    private suspend fun maybeShowPlayReviewPopup(context: Context = appContext) {
        val reviewed = getReviewed(context)
        if (reviewed) return
        val count = getPlayReviewPopCount(context)
        val latestShowTime = getLatestShowPlayReviewTime(context)
        if (count == 0 && latestShowTime == 0) {
            // mark first launching app time
            setLatestShowPlayReviewTime(context)
            return
        }
        val duration = System.currentTimeMillis().milliseconds - latestShowTime.milliseconds
        if (isDurationOvertime(duration, count)) {
            showPlayReviewPopup()
        }
    }

    private fun isDurationOvertime(duration: Duration, count: Int): Boolean {
        if (duration > maxInternal) return true
        if (count == 0) return duration >= 3.days
        if (count == 1) return duration > 10.days
        if (count == 2) return duration > 30.days
        return false
    }

    private fun showPlayReviewPopup() {
        val activity = TopActivityManager.topActiveActivity ?: return
        val manager = ReviewManagerFactory.create(activity)
        manager.requestReviewFlow().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val flow = manager.launchReviewFlow(activity, task.result)
                flow.addOnCompleteListener { result ->
                    Log.d("F_TEST", "OnComplete: ${result.isSuccessful}")
                    Log.i("F_TEST", "OnComplete: ${result.isSuccessful}")
                    if (result.isSuccessful) {
                        onReviewSuccess(activity)
                    } else {
                        onReviewCancel(activity)
                    }
                }.addOnFailureListener { e ->
                    Log.d(
                        "F_TEST",
                        "on Failure: ${e.message}, ${(e as? ReviewException)?.errorCode}"
                    )
                    Log.i(
                        "F_TEST",
                        "on Failure: ${e.message}, ${(e as? ReviewException)?.errorCode}"
                    )
                }
            } else {
                val reviewErrorCode = (task.exception as? ReviewException)?.errorCode
                Log.d("F_TEST", "reviewErrorCode: $reviewErrorCode")
                Log.i("F_TEST", "reviewErrorCode: $reviewErrorCode")
            }
        }
    }

    private fun onReviewSuccess(context: Context) {
        ApplicationScope.launch {
            setReviewed(context)
        }
    }

    private fun onReviewCancel(context: Context) {
        ApplicationScope.launch {
            increasePlayReviewPopCount(context)
            setLatestShowPlayReviewTime(context)
        }
    }

    private suspend fun setReviewed(context: Context) {
        LocalConfigManager.putBoolean(context, LOCAL_KEY_REVIEWED, true)
    }

    private suspend fun getReviewed(context: Context): Boolean {
        return LocalConfigManager.getBoolean(context, LOCAL_KEY_REVIEWED) ?: false
    }

    private suspend fun increasePlayReviewPopCount(context: Context) {
        val count = getPlayReviewPopCount(context) + 1
        LocalConfigManager.putInt(context, LOCAL_KET_REVIEW_POP_COUNT, count)
    }

    private suspend fun getPlayReviewPopCount(context: Context): Int {
        return LocalConfigManager.getInt(context, LOCAL_KET_REVIEW_POP_COUNT) ?: 0
    }

    private suspend fun setLatestShowPlayReviewTime(context: Context) {
        val time = (System.currentTimeMillis() / 1000).toInt()
        LocalConfigManager.putInt(context, LOCAL_KEY_LATEST_SHOW_TIME, time)
    }

    private suspend fun getLatestShowPlayReviewTime(context: Context): Int {
        return LocalConfigManager.getInt(context, LOCAL_KEY_LATEST_SHOW_TIME) ?: 0
    }
}
