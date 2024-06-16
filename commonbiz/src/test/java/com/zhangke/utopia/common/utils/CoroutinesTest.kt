package com.zhangke.fread.common.utils

import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors

fun createMainThreadSurrogate() = Executors.newFixedThreadPool(1).asCoroutineDispatcher()