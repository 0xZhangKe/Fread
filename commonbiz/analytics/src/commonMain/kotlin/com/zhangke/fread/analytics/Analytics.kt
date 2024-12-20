package com.zhangke.fread.analytics

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.analytics.analytics

fun reportPageShow(
    pageName: String,
    paramsBuilder: (TrackingEventDataBuilder.() -> Unit),
) {
    reportToFireBase(EventNames.PAGE_SHOW) {
        put(EventParamsName.PAGE_NAME, pageName)
        paramsBuilder()
    }
}

fun reportClick(
    element: String,
    paramsBuilder: (TrackingEventDataBuilder.() -> Unit) = {},
) {
    reportToFireBase(EventNames.CLICK) {
        put(EventParamsName.ELEMENT, element)
        paramsBuilder()
    }
}

fun reportInfo(
    paramsBuilder: (TrackingEventDataBuilder.() -> Unit),
) {
    reportToFireBase(EventNames.INFO, paramsBuilder)
}

fun reportToFireBase(
    eventName: String,
    paramsBuilder: (TrackingEventDataBuilder.() -> Unit) = {},
) {
    Firebase.analytics.logEvent(eventName, TrackingEventDataBuilder().apply(paramsBuilder).build())
}