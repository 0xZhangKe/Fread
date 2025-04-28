package com.zhangke.framework.composable.bubble

class GlobalBubbleManager {

    private val bubbleLis = mutableListOf<Bubble>()

    fun addBubble(bubble: Bubble){
        bubbleLis += bubble
    }
}
