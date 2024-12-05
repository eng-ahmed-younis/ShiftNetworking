package com.network.networking

import android.content.Context
import com.network.networking.config.NetworkConfig
import com.network.networking.getaway.ApiGateway

/**
 * NetworkInitializer is responsible for initializing the network layer,
 * including setting up the ApiGateway and providing services for network operations.
 */
class NetworkInitializer {

    // Private reference to the ApiGateway instance, which is lazily initialized to ensure it's only set up once.
    private var apiGateway: ApiGateway? = null

    /**
     * Initializes the ApiGateway with the provided [NetworkConfig] and [context].
     * Ensures that the ApiGateway is initialized only once during the lifecycle.
     *
     * This method should be called before attempting to create any network service.
     *
     * @param config The network configuration containing details such as base URL, timeouts, headers, etc.
     * @param context The application context, which may be required for network interceptors or other setup.
     * @return The current instance of [NetworkInitializer] to allow for method chaining.
     */
    fun init(config: NetworkConfig, context: Context): NetworkInitializer {
        // Initialize ApiGateway only if it's not already initialized.
        if (apiGateway == null) {
            apiGateway = ApiGateway.initialize(config, context)
        }
        // Return the current instance to allow method chaining to access createService fun from init
        // append ApiGetaway instance
        return this
    }

    /**
     * Creates and returns an instance of the specified service class.
     * If the ApiGateway is not initialized, an exception will be thrown.
     *
     * This method requires that [init()] be called first to set up the network layer.
     *
     * @param serviceClass The class type of the service to create an instance of.
     * @return A service instance of type [T].
     * @throws IllegalStateException If the [NetworkInitializer] has not been initialized.
     */
    fun <T> createService(serviceClass: Class<T>): T {
        // Ensure ApiGateway is initialized before attempting to create a service.
        return apiGateway?.createService(serviceClass)
            ?: throw IllegalStateException("NetworkInitializer is not initialized. Call init() first.")
    }
}
