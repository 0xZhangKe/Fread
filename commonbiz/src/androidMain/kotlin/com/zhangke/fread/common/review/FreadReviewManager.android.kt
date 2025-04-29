package com.zhangke.fread.common.review

import com.google.android.play.core.review.ReviewException
import com.google.android.play.core.review.ReviewManagerFactory
import com.zhangke.framework.activity.TopActivityManager
import com.zhangke.framework.utils.Log

internal actual fun showAppStoreReviewPopup(
    onReviewSuccess: () -> Unit,
    onReviewCancel: () -> Unit,
) {
    val activity = TopActivityManager.topActiveActivity ?: return
    val manager = ReviewManagerFactory.create(activity)
    manager.requestReviewFlow().addOnCompleteListener { task ->
        if (task.isSuccessful) {
            val flow = manager.launchReviewFlow(activity, task.result)
            flow.addOnCompleteListener { result ->
                if (result.isSuccessful) {
                    onReviewSuccess()
                } else {
                    onReviewCancel()
                }
            }
        } else {
            val reviewErrorCode = (task.exception as? ReviewException)?.errorCode
            Log.i("ReviewManager") { "reviewErrorCode: $reviewErrorCode" }
        }
    }
}
