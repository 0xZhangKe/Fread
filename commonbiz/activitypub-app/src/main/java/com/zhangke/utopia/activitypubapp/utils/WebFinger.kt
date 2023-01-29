package com.zhangke.utopia.activitypubapp.utils

internal class WebFinger(content: String) {

    var value: String? = null
        private set

    var name: String? = null
        private set

    var host: String? = null
        private set

    init {
        value = buildWebFinger(content)
    }

    private fun buildWebFinger(content: String): String? {
        if (!symbolValidate(content)) return null
        val lastAtSymbolIndex = content.lastIndexOf('@')
        if (lastAtSymbolIndex < 0 || lastAtSymbolIndex > content.length - 2) return null
        val maybeHost = content.substring(lastAtSymbolIndex + 1, content.length)
        val host: String =
            if (maybeHost.contains('.')) {
                maybeHost
            } else {
                return null
            }
        if (!hostValidate(host)) return null
        val name = content.removeSuffix("@$host").removePrefix("@")
        if (!nameValidate(name)) return null
        this.name = name
        this.host = host
        return "@$name@$host"
    }

    private fun symbolValidate(content: String): Boolean {
        content.toCharArray().forEach {
            if (!isValidateWebFingerSymbol(it)) return false
        }
        return true
    }

    private fun hostValidate(host: String): Boolean {
        host.toCharArray().forEach {
            if (!isValidateHostSymbol(it)) return false
        }
        return true
    }

    private fun nameValidate(name: String): Boolean {
        name.toCharArray().forEach {
            if (!isValidateNameSymbol(it)) return false
        }
        return true
    }

    private fun isValidateWebFingerSymbol(c: Char): Boolean {
        return isValidateNameSymbol(c)
                || isValidateHostSymbol(c)
                || c == '@'
    }

    private fun isValidateHostSymbol(symbol: Char): Boolean {
        return symbol == '_'
                || symbol == '.'
                || symbol.isDigit()
                || symbol.isLetter()
    }

    private fun isValidateNameSymbol(symbol: Char): Boolean {
        return symbol == '_'
                || symbol.isDigit()
                || symbol.isLetter()
    }

    fun validate(): Boolean {
        return !value.isNullOrEmpty()
    }
}