package com.network.networking.config

import okhttp3.Interceptor

class NetworkBuilder(
    private val baseUrl: String
) {
    private var connectionTimeout: Long = 70L
    private var readTimeout: Long = 70L
    private var writeTimeout: Long = 120L
    private var enableLogging: Boolean = false
    private val headers: MutableMap<String, String> = mutableMapOf()
    private val queryParameters: MutableMap<String, String> = mutableMapOf()
    private val applicationInterceptor: MutableList<Interceptor> = mutableListOf()
    private val networkInterceptor: MutableList<Interceptor> = mutableListOf()


    // Setters for each property
    fun setConnectionTimeout(timeout: Long) = apply { this.connectionTimeout = timeout }
    fun setReadTimeout(timeout: Long) = apply { this.readTimeout = timeout }
    fun setWriteTimeout(timeout: Long) = apply { this.writeTimeout = timeout }
    fun enableLogging(enable: Boolean) = apply { this.enableLogging = enable }
    fun addHeader(key: String, value: String) = apply { this.headers[key] = value }
    fun addQueryParameter(key: String, value: String) = apply { this.queryParameters[key] = value }
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
            enableLogging = enableLogging,
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