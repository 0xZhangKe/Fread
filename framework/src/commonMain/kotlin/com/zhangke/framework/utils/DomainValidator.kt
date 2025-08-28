package com.zhangke.framework.utils

object DomainValidator {

    fun validate(domain: String): Boolean {
        if (domain.isEmpty()) return false
        try {
            if (RegexFactory.domainRegex.matches(domain)) return true
            val idnDomain = IDNUtils().toASCII(domain)
            return RegexFactory.domainRegex.matches(idnDomain)
        } catch (_: Throwable) {
            return false
        }
    }
}
