package com.zhangke.fread.analytics

import com.zhangke.krouter.KRouter

interface LogReporter {

    fun report(eventName: String, parameters: Map<String, String>)
}

private val loggerList: List<LogReporter> by lazy {
    KRouter.getServices()
}

fun reportPageShow(
    pageName: String,
    paramsBuilder: (TrackingEventDataBuilder.() -> Unit),
) {
    reportToLogger(EventNames.PAGE_SHOW) {
        put(EventParamsName.PAGE_NAME, pageName)
        paramsBuilder()
    }
}

fun reportInfo(
    paramsBuilder: (TrackingEventDataBuilder.() -> Unit),
) {
    reportToLogger(EventNames.INFO, paramsBuilder)
}

fun reportToLogger(
    eventName: String,
    paramsBuilder: (TrackingEventDataBuilder.() -> Unit) = {},
) {
    for (reporter in loggerList) {
        reporter.report(eventName, TrackingEventDataBuilder().apply(paramsBuilder).build())
    }
}