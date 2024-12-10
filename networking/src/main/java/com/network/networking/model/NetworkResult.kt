// Package declaration for the `model` classes related to networking operations
package com.network.networking.model

/**
 * A sealed class representing the result of a network operation.
 * This helps in handling different outcomes of a network request in a type-safe manner.
 *
 * @param T The type of the data associated with the result.
 */
sealed class NetworkResult<T> {

    /**
     * Represents a successful network operation with data.
     *
     * @param data The data returned from the network request.
     */
    class NetworkSuccess<T>(val data: T) : NetworkResult<T>()

    /**
     * Represents an error response from the network operation.
     *
     * @param code The error code associated with the network failure (e.g., HTTP status code).
     * @param message An optional message describing the error.
     */
    class NetworkError<T : Any>(val code: String, val message: String?) : NetworkResult<T>()

    /**
     * Represents an exception that occurred during a network operation, such as a timeout or connectivity issue.
     *
     * @param throwable The exception thrown during the network operation.
     */
    class NetworkException<T : Any>(val throwable: Throwable) : NetworkResult<T>()
}
