package com.network.networking.config

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit


data class NetworkConfig(
    val baseUrl: String,
    val connectionTimeout: Long = 70L,
    val readTimeout: Long = 70L,
    val writeTimeout: Long = 120L,
    val enableLogging: Boolean = false,
    val headers: MutableMap<String, String> = mutableMapOf(),// Optional headers || dynamic headers
    val queryParameters: MutableMap<String, String> = mutableMapOf(), // Optional query parameters || dynamic query parameters
    val applicationInterceptor: List<Interceptor> = listOf(), // Optional application interceptors
    val networkInterceptor: List<Interceptor> = listOf() // Optional network interceptors
) {

    private val okhttpBuilder by lazy {
        OkHttpClient.Builder()
            .readTimeout(readTimeout, TimeUnit.SECONDS)
            .writeTimeout(writeTimeout, TimeUnit.SECONDS)
            .connectTimeout(connectionTimeout, TimeUnit.SECONDS)
    }

    private val loggingInterceptor by lazy {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.HEADERS
            level = HttpLoggingInterceptor.Level.BODY
            level = HttpLoggingInterceptor.Level.BASIC
        }
    }

    // build okhttp client with attacked interceptor
    internal fun buildOkHttpClient(context: Context): OkHttpClient {

        val clientBuilder = okhttpBuilder

        // Add logging and Chucker interceptors if logging is enabled
        if (enableLogging){
            clientBuilder
                .addInterceptor(loggingInterceptor)
                .addInterceptor(ChuckerInterceptor.Builder(context).build())
        }

        clientBuilder.addInterceptor { chain ->
            val originalRequest  =  chain.request()
            val originalUrl = originalRequest.url

            // add query parameters to url
            val updatedUrl = originalUrl.newBuilder().apply {
                queryParameters.forEach { (key , value) ->
                    addQueryParameter(key,value)
                }
            }.build()


            // Add headers to the request
            val updatedRequest = originalRequest.newBuilder().apply {
                headers.forEach { (key, value) ->
                    addHeader(key, value)
                }
                url(updatedUrl)
            }.build()

            chain.proceed(updatedRequest)

        }


        // Add user-defined application interceptors
        applicationInterceptor.forEach { clientBuilder.addInterceptor(it) }

        // Add user-defined network interceptors
        networkInterceptor.forEach { clientBuilder.addNetworkInterceptor(it) }

        return clientBuilder.build()

    }
}