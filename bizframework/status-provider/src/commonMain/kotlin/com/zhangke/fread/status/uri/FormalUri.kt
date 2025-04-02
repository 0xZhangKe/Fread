package com.zhangke.fread.status.uri

import com.zhangke.framework.utils.Parcelize
import com.zhangke.framework.utils.PlatformIgnoredOnParcel
import com.zhangke.framework.utils.PlatformParcelable
import com.zhangke.framework.utils.PlatformSerializable
import com.zhangke.framework.utils.UrlEncoder
import com.zhangke.framework.utils.uriString
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
class FormalUri private constructor(
    val host: String,
    /**
     * format like "/xxx"
     */
    private val rawPath: String,
    val queries: Map<String, String>,
) : PlatformParcelable, PlatformSerializable {

    @PlatformIgnoredOnParcel
    @Suppress("MemberVisibilityCanBePrivate")
    val scheme = SCHEME

    @PlatformIgnoredOnParcel
    val path = fixPath(rawPath)

    override fun toString(): String {
        return uriString(
            scheme = scheme,
            host = host,
            path = path,
            queries = queries,
        )
    }

    /**
     * Not encode string
     */
    fun toRawString(): String {
        return uriString(
            scheme = scheme,
            host = host,
            path = path,
            queries = queries,
            encode = false,
        )
    }

    private fun fixPath(path: String): String {
        var newPath = path
        if (!newPath.startsWith('/')) {
            newPath = "/$newPath"
        }
        if (newPath.endsWith('/')) {
            newPath = newPath.substring(0, newPath.length - 1)
        }
        return newPath
    }

    override fun hashCode(): Int {
        var result = host.hashCode()
        result = 31 * result + path.hashCode()
        result = 31 * result + queries.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other == null) return false
        if (other !is FormalUri) return false
        return (host == other.host) && (path == other.path) && (queries == other.queries)
    }

    companion object {

        const val SCHEME = "freadapp"

        fun create(host: String, path: String, queries: Map<String, String>): FormalUri {
            return FormalUri(
                host = host,
                rawPath = path,
                queries = queries,
            )
        }

        fun from(uri: String): FormalUri? {
            val (host, path, queries) = FormalUriParser().parse(uri) ?: return null
            return FormalUri(host, path, queries)
        }
    }
}
