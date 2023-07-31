package com.zhangke.framework.utils

import org.junit.Assert
import org.junit.Test

class UtiUtilsTest {

    @Test
    fun shouldRemovePathPrefixAndSuffix(){
        val uri = uriString(
            scheme = "app",
            host = "zhangke.com",
            path = "/home/",
            queries = mapOf("name" to "zhangke"),
        )
        Assert.assertEquals("app://zhangke.com/home?name=zhangke", uri)
    }

    @Test
    fun shouldRemovePathPrefixAndSuffixWhenEmptyHost(){
        val uri = uriString(
            scheme = "app",
            host = "",
            path = "/home/",
            queries = mapOf("name" to "zhangke"),
        )
        Assert.assertEquals("app://home?name=zhangke", uri)
    }

    @Test
    fun shouldRemovePathSuffixWhenEmptyScheme(){
        val uri = uriString(
            scheme = "",
            host = "zhangke.com",
            path = "/home/",
            queries = mapOf("name" to "zhangke"),
        )
        Assert.assertEquals("zhangke.com/home?name=zhangke", uri)
    }
}
