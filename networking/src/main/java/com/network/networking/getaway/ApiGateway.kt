package com.network.networking.getaway

import android.content.Context
import com.network.networking.config.NetworkConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiGateway private constructor(retrofit: Retrofit) {

    private val retrofitInstance: Retrofit = retrofit

    fun <T> createService(serviceClass: Class<T>): T {
        return retrofitInstance.create(serviceClass)
    }

    companion object {
        fun initialize(config: NetworkConfig, context: Context): ApiGateway {
            val client = config.buildOkHttpClient(context)

            val retrofit = Retrofit.Builder()
                .baseUrl(config.baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return ApiGateway(retrofit)
        }
    }
}