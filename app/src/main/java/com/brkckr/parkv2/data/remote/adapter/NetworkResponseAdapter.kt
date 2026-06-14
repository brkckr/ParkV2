package com.brkckr.parkv2.data.remote.adapter

import com.brkckr.parkv2.domain.util.Resource
import retrofit2.Call
import retrofit2.CallAdapter
import java.lang.reflect.Type

class NetworkResponseAdapter<T : Any>(
    private val successType: Type
) : CallAdapter<T, Call<Resource<T>>> {

    override fun responseType(): Type = successType

    override fun adapt(call: Call<T>): Call<Resource<T>> {
        return NetworkResponseCall(call)
    }
}
