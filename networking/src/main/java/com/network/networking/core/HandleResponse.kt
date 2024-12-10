package com.network.networking.core

import ErrorResponse
import com.google.gson.Gson
import com.network.networking.model.NetworkResult
import retrofit2.HttpException
import retrofit2.Response

/**
 * A function that handles API calls and wraps the results in a [NetworkResult] sealed class.
 * It processes successful responses, errors, and exceptions appropriately.
 */
fun <T : Any> handleApi(
    execute: () -> Response<T> // The API request function
): NetworkResult<T> {
    return try {
        val response = execute()  // Execute the network request
        val body = response.body() // Extract the response body

        // Check if the response is successful and contains a valid body
        if (response.isSuccessful && body != null) {
            NetworkResult.NetworkSuccess(body) // Return successful result
        } else {
            // Handle errors in unsuccessful responses
            response.errorBody()?.let {
                // Parse error body if available
                val gson = Gson()
                // deserializes the specified JSON into an object of the specified class.
                val errorResponse: ErrorResponse = gson.fromJson(
                    it.string(),
                    ErrorResponse::class.java
                )

                // Return network error with code and message from the response
                NetworkResult.NetworkError(
                    code = "${response.code()}",
                    message = errorResponse.message
                )
            } ?: run {
                // In case there is no error body, return a generic error
                NetworkResult.NetworkError(
                    code = "${response.code()}",
                    message = "Unknown error"
                )
            }
        }
    } catch (e: HttpException) {
        // Catch HTTP exceptions (e.g., Unauthorized, Forbidden, etc.)
        println("_unAuthorized2  ${e.code()}")
        NetworkResult.NetworkError(
            code = "${e.code()}",
            message = e.message() // Error message from HttpException
        )
    } catch (e: Throwable) {
        // Catch any other kind of throwable error (e.g., network failure)
        NetworkResult.NetworkException(e) // Return the exception itself
    }
}

// Extension function for handling success case of a network result
suspend fun <T : Any> NetworkResult<T>.onSuccess(
    executable: suspend (T) -> Unit // Block to execute on success
): NetworkResult<T> = apply {
    if (this is NetworkResult.NetworkSuccess<T>) {
        executable(data) // Call the success block with the data
    }
}

// Extension function for handling error case of a network result
suspend fun <T : Any> NetworkResult<T>.onError(
    executable: suspend (code: String, message: String?) -> Unit // Block to execute on error
): NetworkResult<T> = apply {
    if (this is NetworkResult.NetworkError<T>) {
        executable(code, message) // Call the error block with code and message
    }
}

// Extension function for handling exception case of a network result
suspend fun <T : Any> NetworkResult<T>.onException(
    executable: suspend (e: Throwable) -> Unit // Block to execute on exception
): NetworkResult<T> = apply {
    if (this is NetworkResult.NetworkException<T>) {
        executable(throwable) // Call the exception block with the exception
    }
}
