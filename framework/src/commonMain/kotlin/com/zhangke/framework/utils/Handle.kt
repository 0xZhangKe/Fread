package com.zhangke.framework.utils

fun String.prettyHandle(): String = if (this.startsWith('@')) this else "@$this"
