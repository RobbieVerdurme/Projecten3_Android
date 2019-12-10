package be.multinet.model

import retrofit2.Response

/**
 * This class wraps an API response and a local database response.
 */
data class LoadDataResult<R,DB>(
    val apiResponse: Response<R>?,
    val databaseResponse: DB?
)