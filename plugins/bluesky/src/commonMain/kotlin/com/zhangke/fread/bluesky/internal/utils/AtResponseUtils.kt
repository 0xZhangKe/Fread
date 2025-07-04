package com.zhangke.fread.bluesky.internal.utils

import com.zhangke.framework.architect.json.globalJson
import com.zhangke.fread.bluesky.internal.client.expired
import com.zhangke.fread.status.account.AuthenticationFailureException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import sh.christian.ozone.api.response.AtpResponse

fun <T : Any> AtpResponse<T>.toResult(): Result<T> {
    return when (this) {
        is AtpResponse.Success -> Result.success(this.response)
        is AtpResponse.Failure -> Result.failure(AtRequestException.fromResponse(this))
    }
}

data class AtRequestException(
    val statusCode: Int,
    val response: Any?,
    val error: String?,
    val errorMessage: String?,
    val headers: Map<String, String>,
) : Exception() {

    val needAuthFactorTokenRequired: Boolean
        get() = error == ERROR_FACTOR_REQUIRED

    companion object {

        private const val ERROR_FACTOR_REQUIRED = "AuthFactorTokenRequired"

        fun fromResponse(response: AtpResponse.Failure<*>): Throwable {
            if (response.error?.expired == true) {
                return AuthenticationFailureException(response.error!!.message)
            }
            return AtRequestException(
                statusCode = response.statusCode.code,
                response = response.response,
                error = response.error?.error,
                errorMessage = response.error?.message,
                headers = response.headers,
            )
        }
    }
}

internal val bskyJson by lazy {
    Json(globalJson) {
        ignoreUnknownKeys = true
        classDiscriminator = "${'$'}type"
    }
}

internal inline fun <reified T, reified R> T.bskyJson(): R =
    bskyJson.decodeFromJsonElement(bskyJson.encodeToJsonElement(this))
