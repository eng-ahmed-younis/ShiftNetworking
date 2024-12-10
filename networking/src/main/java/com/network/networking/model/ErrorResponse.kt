/**
 * A data class representing the error response structure from a network request.
 *
 * @property code A string representing the error code, typically corresponding to the issue encountered.
 * @property message An optional descriptive message providing details about the error.
 * @property success A boolean indicating whether the operation was successful (usually `false` for an error response).
 */
data class ErrorResponse(
    val code: String,          // The unique error code representing the type of error.
    val message: String?,      // A human-readable description of the error (nullable).
    val success: Boolean       // Indicates the success status; typically `false` for errors.
)
