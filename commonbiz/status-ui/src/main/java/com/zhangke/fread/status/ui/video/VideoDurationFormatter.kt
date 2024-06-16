package com.zhangke.fread.status.ui.video

import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

object VideoDurationFormatter {

    fun formatVideoProgressDesc(playerPosition: Long, durationMs: Long): String {
        val currentDurationDesc = formatVideoDuration(playerPosition)
        val durationDesc = formatVideoDuration(durationMs)
        return "$currentDurationDesc / $durationDesc"
    }

    private fun formatVideoDuration(durationMs: Long): String {
        val builder = StringBuilder()
        val duration = durationMs.milliseconds
        if (duration.isInfinite()) return ""
        val hours = duration.inWholeHours.hours
        if (hours > 0.hours) {
            builder.append(hours.toFormatString(DurationUnit.HOURS))
            builder.append(":")
        }
        val minutes = (duration - hours).inWholeMinutes.minutes
        builder.append(minutes.toFormatString(DurationUnit.MINUTES))
        builder.append(":")
        val seconds = (duration - hours - minutes).inWholeSeconds.seconds
        builder.append(seconds.toFormatString(DurationUnit.SECONDS))
        return builder.toString()
    }

    private fun Duration.toFormatString(unit: DurationUnit): String {
        return String.format("%02d", toInt(unit))
    }
}