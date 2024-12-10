package com.network.networking.adapters

import com.network.networking.core.handleApi
import com.network.networking.model.NetworkResult
import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

/**
 * [NetworkResultCall] is a custom implementation of Retrofit's [Call] interface.
 * It adapts a Retrofit [Call<T>] into a [Call<NetworkResult<T>>], allowing for
 * uniform handling of API responses (success, error, exceptions).
 *
 * @param T The type of the response body expected from the API.
 * @property proxy The original Retrofit [Call] object being wrapped.
 */
class NetworkResultCall<T : Any>(private val proxy: Call<T>) : Call<NetworkResult<T>> {

    /**
     * Creates and returns a copy of the current [NetworkResultCall] instance.
     * The internal proxy [Call] is cloned to ensure a new independent request.
     */
    override fun clone(): Call<NetworkResult<T>> = NetworkResultCall(proxy.clone())

    /**
     * Executes the network call synchronously.
     * Not implemented because synchronous calls are not supported for [NetworkResultCall].
     *
     * @throws NotImplementedError
     */
    override fun execute(): Response<NetworkResult<T>> = throw NotImplementedError()

    /**
     * Checks if the network call has already been executed.
     *
     * @return `true` if the call has been executed, otherwise `false`.
     */
    override fun isExecuted(): Boolean = proxy.isExecuted

    /**
     * Cancels the network call.
     */
    override fun cancel() {
        proxy.cancel()
    }

    /**
     * Checks if the network call has been canceled.
     *
     * @return `true` if the call has been canceled, otherwise `false`.
     */
    override fun isCanceled(): Boolean = proxy.isCanceled

    /**
     * Returns the [Request] associated with this network call.
     *
     * @return The [Request] object describing the HTTP request.
     */
    override fun request(): Request = proxy.request()

    /**
     * Returns the timeout settings for this network call.
     *
     * @return The [Timeout] object containing timeout settings.
     */
    override fun timeout(): Timeout = proxy.timeout()

    /**
     * Executes the network call asynchronously and adapts its response or failure
     * into a [NetworkResult].
     *
     * @param callback A [Callback] to receive the adapted [NetworkResult].
     */
    override fun enqueue(callback: Callback<NetworkResult<T>>) {
        // Enqueue the original proxy call and wrap its response in a custom callback.
        proxy.enqueue(object : Callback<T> {
            /**
             * Handles a successful response from the network call.
             *
             * @param call The original [Call] that was executed.
             * @param response The [Response] returned by the server.
             */
            override fun onResponse(call: Call<T>, response: Response<T>) {
                println("onResponse ${response.message()}") // Debug log
                val networkResult = handleApi { response } // Process response into NetworkResult
                // Pass the adapted result to the original callback
                callback.onResponse(this@NetworkResultCall, Response.success(networkResult))
            }

            /**
             * Handles a network or conversion failure.
             *
             * @param call The original [Call] that was executed.
             * @param t The [Throwable] representing the error or failure.
             */
            override fun onFailure(call: Call<T>, t: Throwable) {
                // Wrap the failure into a NetworkException
                val networkResult = NetworkResult.NetworkException<T>(t)
                Timber.tag("NetworkResultCall").d("API error %s", t.message) // Debug log
                // Pass the adapted result to the original callback
                callback.onResponse(this@NetworkResultCall, Response.success(networkResult))
            }
        })
    }
}
