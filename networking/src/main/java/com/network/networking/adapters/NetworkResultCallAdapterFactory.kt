package com.network.networking.adapters

import com.network.networking.mappers.NetworkResultCallAdapterMapper
import com.network.networking.model.NetworkResult
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import retrofit2.Call
import timber.log.Timber

/** [NetworkResultCallAdapterFactory] CallAdapter.Factory to adapt Retrofit responses into [NetworkResult]
 * .*/
class NetworkResultCallAdapterFactory private constructor() : CallAdapter.Factory() {

    /** [returnType]: The return type of the service method, e.g., Call<NetworkResult<User>>.
     * [annotations]: Annotations applied to the service method, e.g., @GET, @POST.
     * [retrofit]: The Retrofit instance requesting the adapter.
     * */
    override fun get(
        returnType: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        // Validate the return type is parameterized like Call<NetworkResult<User>>
        if (returnType !is ParameterizedType) {
            throw IllegalArgumentException("Return type must be parameterized as Call<NetworkResult<T>>")
        }

        // Ensure the return type is a Call
        if (getRawType(returnType) != Call::class.java) {
            Timber.w("ReturnType is not a Call, skipping: $returnType")
            return null
        }

        /** Extracts the inner type T from Call<T> (e.g., from Call<NetworkResult<User>>,
         * it extracts NetworkResult<User>).
         * Checks if the inner type is NetworkResult
         * */
        val callType = getParameterUpperBound(0, returnType)
        if (getRawType(callType) != NetworkResult::class.java) {
            Timber.w("CallType is not a NetworkResult, skipping: $callType")
            return null
        }

        // Ensure NetworkResult is parameterized
        if (callType !is ParameterizedType) {
            throw IllegalArgumentException("NetworkResult must be parameterized as NetworkResult<T>")
        }

        // Extracts the actual type inside NetworkResult<T> (e.g., from NetworkResult<User>, it extracts User).
        val resultType = getParameterUpperBound(0, callType)
        Timber.d("Successfully created CallAdapter for type: $resultType")

        return NetworkResultCallAdapterMapper(resultType)
    }

    companion object {
        fun create(): NetworkResultCallAdapterFactory = NetworkResultCallAdapterFactory()
    }
}

/** Retrofit will not automatically add Call<> if you don't use it explicitly in your interface.
 * The Call<> is a core part of Retrofit's response handling mechanism
 * [Retrofit] integrates with Kotlin [coroutines] and provides the response directly without wrapping it in Call<>.
 * [Retrofit] integrates with [RxJava], and the response is emitted as an Observable or Single.
 * */