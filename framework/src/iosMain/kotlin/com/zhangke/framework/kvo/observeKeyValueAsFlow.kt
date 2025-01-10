@file:OptIn(ExperimentalForeignApi::class)

package com.zhangke.framework.kvo

import kotlinx.cinterop.COpaquePointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import platform.Foundation.NSKeyValueObservingOptionNew
import platform.Foundation.NSKeyValueObservingOptions
import platform.Foundation.addObserver
import platform.Foundation.removeObserver
import platform.Foundation.valueForKeyPath
import platform.darwin.NSObject

// FIXME: NSObjectObserverProtocol is not defined in iosMain

expect interface WrapNSObjectObserverProtocol {
    fun observeValueForKeyPath(keyPath: String?, ofObject: Any?, change: Map<Any?, *>?, context: COpaquePointer?)
}

private class KVOObserver<T>(nsObject: NSObject, keyPath: String, options: NSKeyValueObservingOptions = NSKeyValueObservingOptionNew) :
    NSObject(),
    WrapNSObjectObserverProtocol {

    private var isAdded: Boolean = false
    private val mutex = Mutex()
    private val _observedValue = MutableSharedFlow<T>(1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    val observedValue = _observedValue.asSharedFlow().onSubscription {
        mutex.withLock {
            if (_observedValue.subscriptionCount.value > 0 && !isAdded) {
                nsObject.addObserver(this@KVOObserver, keyPath, options, null)
                isAdded = true
            }
        }
    }.onCompletion {
        mutex.withLock {
            if (_observedValue.subscriptionCount.value == 0 && isAdded) {
                nsObject.removeObserver(this@KVOObserver, keyPath)
                isAdded = false
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun observeValueForKeyPath(keyPath: String?, ofObject: Any?, change: Map<Any?, *>?, context: COpaquePointer?) {
        val value = (ofObject as NSObject).valueForKeyPath(keyPath!!)
        _observedValue.tryEmit(value as T)
    }
}

/**
 * Starts observing the value of a property using Key-Value observation and returns any updates in a [Flow]
 * @param T the type of the property to be observed
 * @param keyPath the key path, relative to the object receiving this message, of the property to observe.
 * @param options A combination of the [NSKeyValueObservingOptions] values that specifies what is included in observation notifications.
 * @return a [Flow] containing the [T] observed at [keyPath]
 */
fun <T> NSObject.observeKeyValueAsFlow(
    keyPath: String,
    options: NSKeyValueObservingOptions,
): Flow<T> {
    val observer = KVOObserver<T>(this, keyPath, options)
    return observer.observedValue
}

fun <T> NSObject.addObserverSingle(
    keyPath: String,
    options: NSKeyValueObservingOptions = NSKeyValueObservingOptionNew,
    callback: (T) -> Unit,
): NSObject {
    val observer = object : NSObject(), WrapNSObjectObserverProtocol {
        @Suppress("UNCHECKED_CAST")
        override fun observeValueForKeyPath(keyPath: String?, ofObject: Any?, change: Map<Any?, *>?, context: COpaquePointer?) {
            val value = (ofObject as NSObject).valueForKeyPath(keyPath!!)
            callback(value as T)
        }
    }
    addObserver(
        observer,
        keyPath,
        options,
        null,
    )
    return observer
}