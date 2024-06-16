package com.zhangke.fread.status.ui.image

import org.junit.Assert
import org.junit.Test

class BlogImageMediasTest {

    @Test
    fun testDecideFirstImageWeightInHorizontalMode() {
        val style = BlogImageMediaDefault.defaultStyle
        Assert.assertEquals(
            style.minWeightInHorizontal,
            style.decideFirstImageWeightInHorizontalMode(10F)
        )
        Assert.assertEquals(
            style.minWeightInHorizontal,
            style.decideFirstImageWeightInHorizontalMode(1F)
        )
        Assert.assertEquals(
            style.maxWeightInHorizontal,
            style.decideFirstImageWeightInHorizontalMode(0.1F)
        )
        Assert.assertEquals(
            style.maxWeightInHorizontal,
            style.decideFirstImageWeightInHorizontalMode(style.maxWeightInHorizontalThreshold)
        )
        println(style.decideFirstImageWeightInHorizontalMode(0F))
        println(style.decideFirstImageWeightInHorizontalMode(0.5F))
        println(style.decideFirstImageWeightInHorizontalMode(0.6F))
        println(style.decideFirstImageWeightInHorizontalMode(0.65F))
        println(style.decideFirstImageWeightInHorizontalMode(0.7F))
        println(style.decideFirstImageWeightInHorizontalMode(0.8F))
        println(style.decideFirstImageWeightInHorizontalMode(0.9F))
        println(style.decideFirstImageWeightInHorizontalMode(0.95F))
        println(style.decideFirstImageWeightInHorizontalMode(1F))
        println(style.decideFirstImageWeightInHorizontalMode(10F))
    }
}
