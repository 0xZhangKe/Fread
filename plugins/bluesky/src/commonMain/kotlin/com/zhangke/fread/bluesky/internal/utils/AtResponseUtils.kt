package com.zhangke.fread.bluesky.internal.utils

import com.zhangke.framework.architect.json.globalJson
import kotlinx.serialization.json.Json
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

    companion object {

        fun fromResponse(response: AtpResponse.Failure<*>): AtRequestException {
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
