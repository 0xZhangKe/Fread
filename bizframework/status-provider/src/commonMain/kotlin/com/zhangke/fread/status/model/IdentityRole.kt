package com.zhangke.fread.status.model

import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.framework.utils.Parcelize
import com.zhangke.framework.utils.PlatformParcelable
import com.zhangke.framework.utils.PlatformSerializable
import com.zhangke.framework.utils.UrlEncoder
import com.zhangke.fread.status.uri.FormalUri
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

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
    val baseUrl: FormalBaseUrl? = null,
) : PlatformParcelable, PlatformSerializable {

    val nonRole: Boolean
        get() = accountUri == null && baseUrl == null

    companion object {

        val nonIdentityRole by lazy { IdentityRole(null, null) }

        fun decodeFromString(text: String): IdentityRole? {
            return try {
                UrlEncoder.decode(text).let {
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
        UrlEncoder.encode(it)
    }
}
