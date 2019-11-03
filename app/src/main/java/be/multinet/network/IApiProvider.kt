package be.multinet.network

import be.multinet.model.Challenge
import be.multinet.network.Request.LoginRequestBody
import be.multinet.network.Response.UserChallengeResponse
import be.multinet.network.Response.UserDataResponse
import com.auth0.android.jwt.JWT
import retrofit2.Response

interface IApiProvider {
    /**
     * Perform a login with the given [body] as payload
     * If the request is unsuccessful, the bytes of [errorBody][Response.errorBody] can be parsed to JSON and then used for creating an [ErrorResponse]
     * @param userbody the request payload
     * @return a [Response] with [JWT] as data
     */
    suspend fun loginUser(userbody: LoginRequestBody): Response<JWT>

    /**
     * get a [User] from the database
     * If the request is unsuccessful, the bytes of [errorBody][Response.errorBody] can be parsed to JSON and then used for creating an [ErrorResponse]
     * @return a [Response] with [UserDataResponse] as data
     */
    suspend fun getUser(userid: Int) : Response<UserDataResponse>

    /**
     * gets the challenges from the user from the database
     * If the request is unsuccessful, the bytes of [errorBody][Response.errorBody] can be parsed to JSON and then used for creating an [ErrorResponse]
     * @return a [Response] with [Challenge] as data
     */
    suspend fun getChallengesUser(userid: Int) : Response<List<UserChallengeResponse>>
}