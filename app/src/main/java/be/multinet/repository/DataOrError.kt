package be.multinet.repository

/**
 * This enum declares error values, related to loading data.
 * @property NO_ERROR no error occurred.
 * @property OFFLINE there is no network connection.
 * @property API_BAD_REQUEST an api request returned HTTP 400.
 * @property API_UNAUTHORIZED an api request returned HTTP 401.
 * @property API_NOT_FOUND an api request returned HTTP 404.
 * @property API_INTERNAL_SERVER_ERROR an api request returned HTTP 500 OR an otherwise unaccepted response code.
 */
enum class DataError{
    NO_ERROR,
    OFFLINE,
    API_BAD_REQUEST,
    API_UNAUTHORIZED,
    API_NOT_FOUND,
    API_INTERNAL_SERVER_ERROR,
    API_DAILY_CHALLENGE_LIMIT_REACHED,
    API_CHALLENGE_ALREADY_COMPLETED
}

/**
 * This class wraps data of type [T] together with a [DataError].
 */
data class DataOrError<T>(
    val error: DataError = DataError.NO_ERROR,
    val data: T
){
    fun hasError(): Boolean = error != DataError.NO_ERROR
}