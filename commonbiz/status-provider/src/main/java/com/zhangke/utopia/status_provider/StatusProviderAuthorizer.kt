package com.zhangke.utopia.status_provider

object StatusProviderAuthorizer {

    private val uncertifiedEventProcessorList = mutableSetOf<UncertifiedEventProcessor>()

    fun onAuthenticationFailure(authenticationPerformer: () -> Unit) {
        uncertifiedEventProcessorList.forEach {
            it.onAuthenticationFailure(authenticationPerformer)
        }
    }

    fun registerUncertifiedEventProcessor(processor: UncertifiedEventProcessor) {
        uncertifiedEventProcessorList.add(processor)
    }

    fun unregisterUncertifiedProcessor(processor: UncertifiedEventProcessor) {
        uncertifiedEventProcessorList.remove(processor)
    }
}

interface UncertifiedEventProcessor {

    fun onAuthenticationFailure(authenticationPerformer: () -> Unit)
}