package com.network.networking.getaway

import android.content.Context
import com.network.networking.adapters.NetworkResultCallAdapterFactory
import com.network.networking.core.NetworkConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// ApiGateway is responsible for initializing retrofit and providing Retrofit instances for making network requests
class ApiGateway private constructor(retrofit: Retrofit) {

    // The Retrofit instance used to create service classes and make network calls
    private val retrofitInstance: Retrofit = retrofit

    /**
     * Creates and returns a service instance of the provided class type.
     *
     * @param serviceClass The class type of the service to create.
     * @return A service instance of type [T].
     */
    fun <T> createService(serviceClass: Class<T>): T {
        return retrofitInstance.create(serviceClass) // Use Retrofit to create the service
    }

    companion object {
        /**
         * Initializes the ApiGateway with the provided [NetworkConfig] and [Context].
         * This method creates a Retrofit instance using the provided configuration.
         *
         * @param config The network configuration including timeouts, base URL, interceptors, etc.
         * @param context The context to be passed to the OkHttpClient for interceptors like Chucker.
         * @return An instance of ApiGateway.
         */
        fun initialize(config: NetworkConfig, context: Context): ApiGateway {
            // Build OkHttpClient using the provided configuration (e.g., timeouts, logging, headers, etc.)
            val client = config.buildOkHttpClient(context)

            // Create a Retrofit instance with the provided base URL and OkHttpClient
            val retrofit = Retrofit.Builder()
                .baseUrl(config.baseUrl) // Set the base URL for API requests
                .client(client) // Set the OkHttpClient for network calls
                .addConverterFactory(GsonConverterFactory.create()) // Add Gson as the converter factory for JSON responses
                .addCallAdapterFactory(NetworkResultCallAdapterFactory.create()) // Add NetworkResultCallAdapterFactory for handling API responses
                .build() // Build the Retrofit instance

            return ApiGateway(retrofit) // Return an instance of ApiGateway with the Retrofit instance
        }
    }
}
