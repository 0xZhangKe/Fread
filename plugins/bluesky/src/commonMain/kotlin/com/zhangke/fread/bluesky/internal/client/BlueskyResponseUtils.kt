package com.zhangke.fread.bluesky.internal.client

import sh.christian.ozone.api.response.AtpErrorDescription
import sh.christian.ozone.api.response.AtpResponse

private fun <T : Any> AtpResponse<T>.toResult(): Result<T> {
    return when (this) {
        is AtpResponse.Success -> Result.success(this.response)
        is AtpResponse.Failure -> Result.failure(BlueskyApiException.fromResponse(this))
    }
}

data class BlueskyApiException(
    val response: Any?,
    val error: AtpErrorDescription?,
    val headers: Map<String, String>,
) : RuntimeException() {

    companion object {

        fun <T : Any> fromResponse(failure: AtpResponse.Failure<T>): BlueskyApiException {
            return BlueskyApiException(
                response = failure.response,
                error = failure.error,
                headers = failure.headers,
            )
        }
    }
}

val AtpErrorDescription.expired: Boolean get() = error == "ExpiredToken"
