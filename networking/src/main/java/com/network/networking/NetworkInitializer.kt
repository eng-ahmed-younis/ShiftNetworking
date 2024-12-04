package com.network.networking

import android.content.Context
import com.network.networking.config.NetworkConfig
import com.network.networking.getaway.ApiGateway

// NetworkInitializer is responsible for initializing the network layer and providing services.
class NetworkInitializer {

    // Private reference to the ApiGateway instance, which is initialized only once.
    private var apiGateway: ApiGateway? = null

    /**
     * Initializes the ApiGateway with the provided NetworkConfig and context.
     * This method ensures that the ApiGateway is only initialized once.
     *
     * @param config The network configuration including base URL, timeouts, headers, etc.
     * @param context The application context, which may be required for certain interceptors.
     */
    fun init(config: NetworkConfig, context: Context) {
        // Only initialize ApiGateway if it's not already initialized.
        if (apiGateway == null) {
            apiGateway = ApiGateway.initialize(config, context)
        }
    }

    /**
     * Creates and returns a service instance of the provided service class.
     * If the ApiGateway has not been initialized, an exception is thrown.
     *
     * @param serviceClass The service class to create an instance of.
     * @return A service instance of type [T].
     * @throws IllegalStateException If the NetworkInitializer has not been initialized yet.
     */
    fun <T> createService(serviceClass: Class<T>): T {
        // Check if ApiGateway is initialized; if not, throw an exception.
        return apiGateway?.createService(serviceClass)
            ?: throw IllegalStateException("NetworkInitializer is not initialized. Call init() first.")
    }
}
