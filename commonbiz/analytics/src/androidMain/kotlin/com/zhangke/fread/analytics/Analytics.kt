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
    reportToFireBase(EventNames.PAGE_SHOW, bundle)
}

fun reportClick(
    element: String,
    paramsBuilder: (TrackingEventDataBuilder.() -> Unit) = {},
) {
    val builder = TrackingEventDataBuilder()
    builder.paramsBuilder()
    val bundle = builder.toBundle()
    bundle.putElement(element)
    reportToFireBase(EventNames.CLICK, bundle)
}

fun reportInfo(
    paramsBuilder: (TrackingEventDataBuilder.() -> Unit) = {},
){
    val builder = TrackingEventDataBuilder()
    builder.paramsBuilder()
    val bundle = builder.toBundle()
    reportToFireBase(EventNames.INFO, bundle)
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
