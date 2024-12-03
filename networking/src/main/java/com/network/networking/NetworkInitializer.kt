package com.network.networking

import android.content.Context
import com.network.networking.config.NetworkConfig
import com.network.networking.getaway.ApiGateway

class NetworkInitializer {

    private var apiGateway: ApiGateway? = null

    fun init(config: NetworkConfig, context: Context) {
        if (apiGateway == null) {
            apiGateway = ApiGateway.initialize(config, context)
        }
    }

    fun <T> createService(serviceClass: Class<T>): T {
        return apiGateway?.createService(serviceClass)
            ?: throw IllegalStateException("NetworkInitializer is not initialized. Call init() first.")
    }

}