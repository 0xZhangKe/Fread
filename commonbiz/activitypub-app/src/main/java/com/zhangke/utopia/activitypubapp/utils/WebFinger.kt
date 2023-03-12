package com.zhangke.utopia.activitypubapp.utils

import com.zhangke.framework.utils.RegexFactory

/**
 * Supported:
 * - jw@jakewharton.com
 * - @jw@jakewharton.com
 * - acct:@jw@jakewharton.com
 * - https://m.cmx.im/@jw@jakewharton.com
 * - https://m.cmx.im/@AtomZ
 * - m.cmx.im/@jw@jakewharton.com
 * - jakewharton.com/@jw
 */
internal class WebFinger private constructor(
    val name: String,
    val host: String,
) {

    override fun toString(): String {
        return "@$name@$host"
    }

    companion object {

        fun create(content: String): WebFinger? {
            if (content.isBlank()) return null
            return createAsAcct(content) ?: createAsUrl(content)
        }

        private fun createAsAcct(content: String): WebFinger? {
            val maybeAcct = content
                .removePrefix("acct:")
                .removePrefix("@")
            val split = maybeAcct.split('@')
            if (split.size != 2) return null
            val name = split[0]
            if (!nameValidate(name)) return null
            val host = split[1]
            if (!hostValidate(host)) return null
            return WebFinger(name, host)
        }

        private fun createAsUrl(content: String): WebFinger? {
            val maybeUrl = content
                .removePrefix("http://")
                .removePrefix("https://")
            val split = maybeUrl.split('/')
            if (split.size != 2) return null
            val urlHost = split[0]
            if (!hostValidate(urlHost)) return null
            val maybeAcct = split[1].removePrefix("@")
            return if (maybeAcct.contains('@')) {
                createAsAcct(maybeAcct)
            } else {
                createAsAcct("$maybeAcct@$urlHost")
            }
        }

        private fun hostValidate(host: String): Boolean {
            return RegexFactory.getDomainRegex().matches(host)
        }

        private fun nameValidate(name: String): Boolean {
            if (name.isEmpty()) return false
            name.toCharArray().forEach {
                if (!isValidateNameSymbol(it)) return false
            }
            return true
        }

        private fun isValidateNameSymbol(symbol: Char): Boolean {
            return symbol == '_'
                    || symbol.isDigit()
                    || symbol.isLetter()
        }
    }
}