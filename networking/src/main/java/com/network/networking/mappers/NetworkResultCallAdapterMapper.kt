package com.network.networking.mappers

import com.network.networking.adapters.NetworkResultCall
import com.network.networking.model.NetworkResult
import retrofit2.Call
import retrofit2.CallAdapter
import java.lang.reflect.Type

class NetworkResultCallAdapterMapper(
    private val resultType: Type
) : CallAdapter<Type, Call<NetworkResult<Type>>> {
    override fun responseType(): Type = resultType


    override fun adapt(call: Call<Type>): Call<NetworkResult<Type>> {
        return NetworkResultCall(call)
    }
}