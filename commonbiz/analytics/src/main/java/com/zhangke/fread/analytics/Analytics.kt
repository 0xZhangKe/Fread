package com.zhangke.fread.analytics

import android.os.Bundle
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics

fun reportPageShow(
    pageName: String,
    paramsBuilder: (TrackingEventDataBuilder.() -> Unit) = {},
) {
    val builder = TrackingEventDataBuilder()
    builder.paramsBuilder()
    val bundle = builder.toBundle()
    bundle.putPageName(pageName)
    reportToFireBase(EventNames.pageShow, bundle)
}

fun report(eventName: String, paramsBuilder: Bundle.() -> Unit) {
    val bundle = Bundle()
    bundle.paramsBuilder()
    reportToFireBase(eventName, bundle)
}

private fun reportToFireBase(eventName: String, bundle: Bundle) {
    Log.d("DataTracking", "reportToFireBase: $eventName, $bundle")
    Firebase.analytics.logEvent(eventName, bundle)
}
