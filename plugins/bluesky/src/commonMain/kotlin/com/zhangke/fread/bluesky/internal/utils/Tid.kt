@file:OptIn(ExperimentalTime::class)

package com.zhangke.fread.bluesky.internal.utils

import kotlin.random.Random
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

object Tid {

    private const val BASE_32_CHARS = "234567abcdefghijklmnopqrstuvwxyz"

    fun generateTID(): String {
        val now = Clock.System.now()
        val currentTimeMicros = (now.epochSeconds * 1_000_000) + (now.nanosecondsOfSecond / 1_000)
        val clockIdentifier = Random.nextBits(10).toLong()
        val tidNumber = (currentTimeMicros shl 10) or clockIdentifier
        return encodeBase32(tidNumber)
    }

    private fun encodeBase32(number: Long): String {
        val base32Chars = BASE_32_CHARS
        val stringBuilder = StringBuilder()
        var num = number
        for (i in 0 until 13) {
            stringBuilder.append(base32Chars[(num and 0x1F).toInt()])
            num = num shr 5
        }
        return stringBuilder.toString().reversed()
    }
}
