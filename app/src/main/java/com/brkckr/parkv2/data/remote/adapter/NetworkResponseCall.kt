package com.brkckr.parkv2.data.remote.adapter

import com.brkckr.parkv2.R
import com.brkckr.parkv2.domain.util.Resource
import com.brkckr.parkv2.domain.util.UiText
import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NetworkResponseCall<T : Any>(
    private val delegate: Call<T>
) : Call<Resource<T>> {

    override fun enqueue(callback: Callback<Resource<T>>) {
        // handle retrofit callback and map to resource
        delegate.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                val body = response.body()
                val error = response.errorBody()

                if (response.isSuccessful) {
                    if (body != null) {
                        callback.onResponse(
                            this@NetworkResponseCall,
                            Response.success(Resource.Success(body))
                        )
                    } else {
                        callback.onResponse(
                            this@NetworkResponseCall,
                            Response.success(Resource.Error(UiText.StringResource(R.string.empty_response_body)))
                        )
                    }
                } else {
                    val message = error?.string() ?: response.message()
                    callback.onResponse(
                        this@NetworkResponseCall,
                        Response.success(Resource.Error(
                            UiText.StringResource(R.string.error_with_code, listOf(response.code(), message))
                        ))
                    )
                }
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                val uiText = t.localizedMessage?.let { UiText.DynamicString(it) } 
                    ?: UiText.StringResource(R.string.network_error)
                callback.onResponse(
                    this@NetworkResponseCall,
                    Response.success(Resource.Error(uiText))
                )
            }
        })
    }

    override fun isExecuted() = delegate.isExecuted
    override fun clone() = NetworkResponseCall(delegate.clone())
    override fun isCanceled() = delegate.isCanceled
    override fun cancel() = delegate.cancel()
    override fun execute(): Response<Resource<T>> = throw UnsupportedOperationException("NetworkResponseCall doesn't support execute")
    override fun request(): Request = delegate.request()
    override fun timeout(): Timeout = delegate.timeout()
}