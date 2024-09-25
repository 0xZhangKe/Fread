package com.zhangke.fread.analytics

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
    paramsBuilder: (TrackingEventDataBuilder.() -> Unit) = {},
) {
    reportToFireBase(EventNames.INFO, paramsBuilder)
}

expect fun reportToFireBase(
    eventName: String,
    paramsBuilder: (TrackingEventDataBuilder.() -> Unit) = {},
)