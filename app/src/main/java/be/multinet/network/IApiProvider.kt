package be.multinet.network

import be.multinet.model.Challenge
import be.multinet.model.Therapist
import be.multinet.network.Request.CompleteChallengeRequestBody
import be.multinet.network.Request.LoginRequestBody
import be.multinet.network.Response.*
import com.auth0.android.jwt.JWT
import retrofit2.Response

interface IApiProvider {
    /**
     * Perform a login with the given [body] as payload
     * If the request is unsuccessful, the bytes of [errorBody][Response.errorBody] can be parsed to JSON
     * @param userbody the request payload
     * @return a [Response] with [JWT] as data
     */
    suspend fun loginUser(userbody: LoginRequestBody): Response<String>

    /**
     * get a [User] from the database
     * If the request is unsuccessful, the bytes of [errorBody][Response.errorBody] can be parsed to JSON
     * @return a [Response] with [UserDataResponse] as data
     */
    suspend fun getUser(userid: Int) : Response<UserDataResponse>

    /**
     * gets the challenges from the user from the database
     * If the request is unsuccessful, the bytes of [errorBody][Response.errorBody] can be parsed to JSON
     * @return a [Response] with [Challenge] as data
     */
    suspend fun getChallengesUser(userid: Int) : Response<List<UserChallengeResponse>>

    /**
     * give the database the challenge that has to be completed
     * If the request is unsuccessful, the bytes of [errorBody][Response.errorBody] can be parsed to JSON
     */
    suspend fun completeChallenge(token :String, challengeRequestBody: CompleteChallengeRequestBody) : Response<CompleteChallengeResponse>


    /**
     * give the database the challenge that has to be completed
     * If the request is unsuccessful, the bytes of [errorBody][Response.errorBody] can be parsed to JSON
     */
    suspend fun getTherapists(token:String, userid: Int) : Response<List<TherapistResponse>>
}