package com.zhangke.activitypub.utils

import com.google.gson.Gson
import com.zhangke.activitypub.entry.ActivityPubErrorEntry
import com.zhangke.activitypub.exception.ActivityPubHttpException
import okhttp3.Request
import okio.Timeout
import retrofit2.*
import java.lang.RuntimeException
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * Created by ZhangKe on 2022/12/3.
 */
internal class ResultCallAdapterFactory(private val gson: Gson) : CallAdapter.Factory() {

    override fun get(
        returnType: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        // Kotlin suspend function will wrap result with retrofit.Call
        if (Call::class.java != getRawType(returnType)) return null
        if (returnType !is ParameterizedType) {
            return null
        }
        val responseType = getParameterUpperBound(0, returnType)
        if (responseType !is ParameterizedType) return null
        val responseRawType = getRawType(responseType)
        if (!Result::class.java.isAssignableFrom(responseRawType)) {
            return null
        }
        return ResultCallAdapter(gson, getParameterUpperBound(0, responseType))
    }
}

private class ResultCallAdapter(private val gson: Gson, private val type: Type) :
    CallAdapter<Any, Call<Result<*>>> {

    override fun responseType(): Type = type

    @Suppress("UNCHECKED_CAST")
    override fun adapt(call: Call<Any>): Call<Result<*>> {
        return ResultCall(call, gson) as Call<Result<*>>
    }
}

private class ResultCall<S>(private val delegate: Call<S>, private val gson: Gson) :
    Call<Result<S>> {

    override fun enqueue(callback: Callback<Result<S>>) {
        return delegate.enqueue(object : Callback<S> {

            override fun onResponse(call: Call<S>, response: Response<S>) {
                if (response.isSuccessful && response.body() != null) {
                    val result = Result.success(response.body()!!)
                    callback.onResponse(this@ResultCall, Response.success(response.code(), result))
                } else {
                    val code = response.code()
                    val errorMessage = response.errorBody()?.string()
                    val errorEntry: ActivityPubErrorEntry? =
                        errorMessage?.takeIf { it.isNotEmpty() }?.let {
                            try {
                                gson.fromJson(it, ActivityPubErrorEntry::class.java)
                            } catch (e: Throwable) {
                                null
                            }
                        }
                    val exception: Exception = when (code) {
                        in 400..499 -> ActivityPubHttpException.RequestIllegalException(
                            errorEntry,
                            errorMessage
                        )
                        in 500..599 -> ActivityPubHttpException.ServerInternalException(
                            errorEntry,
                            errorMessage
                        )
                        else -> RuntimeException(errorMessage, null)
                    }
                    callback.onResponse(this@ResultCall, Response.success(Result.failure(exception)))
                }
            }

            override fun onFailure(p0: Call<S>, p1: Throwable) {
                callback.onResponse(this@ResultCall, Response.success(Result.failure(p1)))
            }
        })
    }

    override fun isExecuted(): Boolean = delegate.isExecuted

    override fun clone(): Call<Result<S>> {
        return ResultCall(delegate.clone(), gson)
    }

    override fun cancel() {
        delegate.cancel()
    }

    override fun isCanceled(): Boolean = delegate.isCanceled

    override fun execute(): Response<Result<S>> {
        throw UnsupportedOperationException("ResponseCall not support execute")
    }

    override fun request(): Request = delegate.request()

    override fun timeout(): Timeout = delegate.timeout()
}