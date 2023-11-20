package com.zhangke.framework.utils

import org.junit.Test

class RegexFactoryTest {

    @Test
    fun test(){
        val find = RegexFactory.getDomainRegex().find("https://m3.material.io")
        println(find?.value)
    }
}