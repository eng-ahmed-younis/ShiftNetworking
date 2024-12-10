package com.network.networking.core

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

/**
 * NetworkConfig Class
 *
 * Created by Ahmed Aly.
 *
 * This class is responsible for configuring network settings, including timeouts, headers,
 * query parameters, interceptors, and logging behavior for making network requests.
 *
 * It provides methods to customize and build the OkHttpClient used for network calls.
 */

data class NetworkConfig(
    val baseUrl: String, // The base URL for API requests (required)
    private val connectionTimeout: Long = 70L, // Timeout duration for establishing a connection (default 70s)
    private val readTimeout: Long = 70L, // Timeout duration for reading from the connection (default 70s)
    private val writeTimeout: Long = 120L, // Timeout duration for writing to the connection (default 120s)
    private val isChuckerInterceptorEnabled: Boolean = false, // Flag to enable/disable Chucker interceptor (default is false)
    private val isLoggingEnabled: Boolean = false, // Flag to enable/disable HTTP request/response logging (default is false)
    private val headers: MutableMap<String, String> = mutableMapOf(), // Optional headers to be added to every request
    private val queryParameters: MutableMap<String, String> = mutableMapOf(), // Optional query parameters to be added to each request URL
    private val applicationInterceptor: List<Interceptor> = listOf(), // List of application-level interceptors (e.g., authentication interceptors)
    private val networkInterceptor: List<Interceptor> = listOf() // List of network-level interceptors (e.g., caching interceptors)
) {

    // Lazy initialization of OkHttpClient builder to prevent unnecessary object creation
    private val okhttpBuilder by lazy {
        OkHttpClient.Builder()
            .readTimeout(readTimeout, TimeUnit.SECONDS) // Set the read timeout duration
            .writeTimeout(writeTimeout, TimeUnit.SECONDS) // Set the write timeout duration
            .connectTimeout(
                connectionTimeout,
                TimeUnit.SECONDS
            ) // Set the connection timeout duration
    }

    // Lazy initialization of the logging interceptor (if enabled)
    private val loggingInterceptor by lazy {
        HttpLoggingInterceptor().apply {
            // Choose the logging level based on whether logging is enabled
            level = if (isLoggingEnabled) {
                HttpLoggingInterceptor.Level.BODY // Log both request and response body if logging is enabled
            } else {
                HttpLoggingInterceptor.Level.NONE // Disable logging if not enabled
            }
        }
    }

    // Method to build and return an OkHttpClient instance with applied configurations
    internal fun buildOkHttpClient(context: Context): OkHttpClient {
        val clientBuilder = okhttpBuilder

        // If logging is enabled, add both the logging and Chucker interceptors
        when {
            (isLoggingEnabled && isChuckerInterceptorEnabled) -> {
                clientBuilder
                    .addInterceptor(loggingInterceptor) // Add the logging interceptor for HTTP request/response logging
                    .addInterceptor(
                        ChuckerInterceptor.Builder(context).build()
                    ) // Add Chucker for network traffic inspection
            }

            (isLoggingEnabled && !isChuckerInterceptorEnabled) -> {
                clientBuilder
                    .addInterceptor(loggingInterceptor) // Add the logging interceptor for HTTP request/response logging
            }

            (!isLoggingEnabled && isChuckerInterceptorEnabled) -> {
                clientBuilder
                    .addInterceptor(
                        ChuckerInterceptor.Builder(context).build()
                    ) // Add Chucker for network traffic inspection
            }
        }

        // Add an interceptor to attach query parameters and headers to the request before it is sent
        clientBuilder.addInterceptor { chain ->
            val originalRequest = chain.request() // The original request object
            val originalUrl = originalRequest.url // Get the original URL of the request

            // Build a new URL by adding any query parameters defined in the configuration
            val updatedUrl = originalUrl.newBuilder().apply {
                queryParameters.forEach { (key, value) ->
                    addQueryParameter(key, value) // Add each query parameter to the URL
                }
            }.build()

            // Build a new request by adding the headers and the updated URL to the original request
            val updatedRequest = originalRequest.newBuilder().apply {
                headers.forEach { (key, value) ->
                    addHeader(key, value) // Add each header to the request
                }
                url(updatedUrl) // Set the updated URL with query parameters
            }.build()

            // Proceed with the request chain using the updated request
            chain.proceed(updatedRequest)
        }

        // Add any custom application interceptors to the client builder
        applicationInterceptor.forEach { clientBuilder.addInterceptor(it) }

        // Add any custom network interceptors to the client builder
        networkInterceptor.forEach { clientBuilder.addNetworkInterceptor(it) }

        // Finally, build and return the OkHttpClient with all the configurations applied
        return clientBuilder.build()
    }


    class Builder(
        private val baseUrl: String
    ) {
        private var connectionTimeout: Long = 70L
        private var readTimeout: Long = 70L
        private var writeTimeout: Long = 120L
        private var enableLogging: Boolean = false
        private var enableChucker: Boolean = false
        private val headers: MutableMap<String, String> = mutableMapOf()
        private val queryParameters: MutableMap<String, String> = mutableMapOf()
        private val applicationInterceptor: MutableList<Interceptor> = mutableListOf()
        private val networkInterceptor: MutableList<Interceptor> = mutableListOf()


        // Setters for each property
        fun setConnectionTimeout(timeout: Long) = apply { this.connectionTimeout = timeout }
        fun setReadTimeout(timeout: Long) = apply { this.readTimeout = timeout }
        fun setWriteTimeout(timeout: Long) = apply { this.writeTimeout = timeout }
        fun enableLogging(enable: Boolean) = apply { this.enableLogging = enable }
        fun enableChucker(enable: Boolean) = apply { this.enableChucker = enable }
        fun addHeader(key: String, value: String) = apply { this.headers[key] = value }
        fun addQueryParameter(key: String, value: String) =
            apply { this.queryParameters[key] = value }

        fun addApplicationInterceptor(interceptor: Interceptor) =
            apply { this.applicationInterceptor.add(interceptor) }

        fun addNetworkInterceptor(interceptor: Interceptor) =
            apply { this.networkInterceptor.add(interceptor) }


        // Build method to create the NetworkConfig instance
        fun build(): NetworkConfig {
            return NetworkConfig(
                baseUrl = baseUrl,
                connectionTimeout = connectionTimeout,
                readTimeout = readTimeout,
                writeTimeout = writeTimeout,
                isLoggingEnabled = enableLogging,
                isChuckerInterceptorEnabled = enableChucker,
                headers = headers,
                queryParameters = queryParameters,
                applicationInterceptor = applicationInterceptor,
                networkInterceptor = networkInterceptor
            ).run {
                // We use run here to operate on the object itself if needed
                // For example, logging the final configuration before returning
                println("NetworkConfig built with baseUrl: $baseUrl")
                this // return the NetworkConfig instance
            }
        }

    }

}
