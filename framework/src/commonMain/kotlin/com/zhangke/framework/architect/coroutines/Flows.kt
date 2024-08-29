package com.zhangke.framework.architect.coroutines

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch

/**
 * Created by ZhangKe on 2022/10/14.
 */

fun <T> Flow<T>.collectWithLifecycle(lifecycle: LifecycleOwner, collector: FlowCollector<T>) {
    lifecycle.lifecycleScope
        .launch {
            collect(collector)
        }
}