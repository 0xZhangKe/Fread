package com.zhangke.fread.analytics

import android.os.Bundle
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics

internal actual fun reportToFireBase(
    eventName: String,
    paramsBuilder: (TrackingEventDataBuilder.() -> Unit),
) {
    reportToFireBase(
        eventName = eventName,
        bundle = TrackingEventDataBuilder().apply(paramsBuilder).build().toBundle(),
    )
}

fun report(eventName: String, paramsBuilder: Bundle.() -> Unit) {
    reportToFireBase(
        eventName = eventName,
        bundle = Bundle().apply(paramsBuilder),
    )
}

private fun reportToFireBase(
    eventName: String,
    bundle: Bundle,
) {
    Log.d("DataTracking", "reportToFireBase: $eventName, $bundle")
    Firebase.analytics.logEvent(eventName, bundle)
}

private fun Map<String, String>.toBundle(): Bundle {
    val bundle = Bundle()
    forEach { (key, value) ->
        bundle.putString(key, value)
    }
    return bundle
}
