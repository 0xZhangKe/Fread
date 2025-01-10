package com.zhangke.framework.kvo

import kotlinx.coroutines.flow.Flow
import platform.Foundation.NSKeyValueObservingOptionNew
import platform.Foundation.NSKeyValueObservingOptions
import platform.darwin.NSObject

// FIXME: NSObjectObserverProtocol is not defined in iosMain
expect fun <T> NSObject.observeKeyValueAsFlow(
    keyPath: String,
    options: NSKeyValueObservingOptions = NSKeyValueObservingOptionNew,
): Flow<T>
