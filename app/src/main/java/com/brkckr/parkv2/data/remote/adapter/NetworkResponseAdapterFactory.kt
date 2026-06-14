package com.brkckr.parkv2.data.remote.adapter

import com.brkckr.parkv2.domain.util.Resource
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class NetworkResponseAdapterFactory : CallAdapter.Factory() {

    override fun get(
        returnType: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        // handle retrofit resource call adapter
        if (Call::class.java != getRawType(returnType)) {
            return null
        }

        check(returnType is ParameterizedType) {
            "Return type must be parameterized as Call<Resource<<T>>"
        }

        val responseType = getParameterUpperBound(0, returnType)

        if (getRawType(responseType) != Resource::class.java) {
            return null
        }

        check(responseType is ParameterizedType) {
            "Response type must be parameterized as Resource<T>"
        }

        val successBodyType = getParameterUpperBound(0, responseType)

        return NetworkResponseAdapter<Any>(successBodyType)
    }
}