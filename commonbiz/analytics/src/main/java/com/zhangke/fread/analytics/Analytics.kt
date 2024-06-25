package com.zhangke.fread.analytics

import android.os.Bundle
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics

fun reportPageShow(
    pageName: String,
    paramsBuilder: (TrackingEventDataBuilder.() -> Unit) = {},
) {
    val builder = TrackingEventDataBuilder()
    builder.paramsBuilder()
    val bundle = builder.toBundle()
    bundle.putString(EventParamsName.pageName, pageName)
    reportToFireBase(EventNames.pageShow, bundle)
}

fun report(eventName: String, paramsBuilder: Bundle.() -> Unit) {
    val bundle = Bundle()
    bundle.paramsBuilder()
    reportToFireBase(eventName, bundle)
}

private fun reportToFireBase(eventName: String, bundle: Bundle) {
    Firebase.analytics.logEvent(eventName, bundle)
}
