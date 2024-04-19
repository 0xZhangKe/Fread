package com.zhangke.utopia.status.model

import android.os.Parcelable
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.status.uri.FormalUri
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.net.URLDecoder
import java.net.URLEncoder

/**
 * 身份角色信息，表示执行后续操作需要使用的身份。
 * 规则如下：有 accountUri 就用该账户身份操作。
 * 没有 accountUri 但有 baseUrl 就用 baseUrl 身份操作。
 * 两者都没有则使用随机挑选的服务器身份。
 */
@Parcelize
@Serializable
data class IdentityRole(
    val accountUri: FormalUri?,
    val baseUrl: FormalBaseUrl?,
) : Parcelable {

    val nonRole: Boolean
        get() = accountUri == null && baseUrl == null

    companion object {

        val nonIdentityRole = IdentityRole(null, null)

        fun decodeFromString(text: String): IdentityRole? {
            return try {
                URLDecoder.decode(text, Charsets.UTF_8.name()).let {
                    Json.decodeFromString(serializer(), it)
                }
            } catch (e: Throwable) {
                null
            }
        }
    }
}

fun IdentityRole.encodeToUrlString(): String {
    return Json.encodeToString(IdentityRole.serializer(), this).let {
        URLEncoder.encode(it, Charsets.UTF_8.name())
    }
}
