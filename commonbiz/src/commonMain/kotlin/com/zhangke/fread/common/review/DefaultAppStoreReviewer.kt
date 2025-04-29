package com.zhangke.fread.common.review

interface DefaultAppStoreReviewer {

    fun showAppStoreReviewPopup(
        onReviewSuccess: () -> Unit,
        onReviewCancel: () -> Unit,
    )
}
