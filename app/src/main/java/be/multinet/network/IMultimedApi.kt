package be.multinet.network

import be.multinet.model.Therapist
import be.multinet.network.Request.CompleteChallengeRequestBody
import be.multinet.network.Request.LoginRequestBody
import be.multinet.network.Response.Ok
import be.multinet.network.Response.TherapistResponse
import be.multinet.network.Response.UserChallengeResponse
import be.multinet.network.Response.UserDataResponse
import com.auth0.android.jwt.JWT
import retrofit2.Response
import retrofit2.http.*

/**

 * The interface which provides methods to get result of the Multimed webservice

 */
interface IMultimedApi {
    /**
     * login a user
     */
    @POST("Account")
    suspend fun loginUser(@Body userbody: LoginRequestBody) : Response<JWT>

    /**
     * get data from a user
     */
    @GET("User/{id}")
    suspend fun getUser(@Path("id") userid: Int) : Response<UserDataResponse>

    /**
     * get the list of challenges from the user
     */
    @GET("challenge/user/{id}")
    suspend fun getChallengesUser(@Path("id") userid: Int) : Response<List<UserChallengeResponse>>

    /**
     * update the challenge to complete
     */
    @POST("challenge/complete")
    suspend fun completeChallenge(@Header("Authorization") token:String,@Body challengeRequestBody: CompleteChallengeRequestBody) : Response<Ok>


    /**
     * get the list of challenges from the user
     */
    @GET("users/therapist/{id}")
    suspend fun getTherapists(@Header("Authorization")token:String, @Path("id") userid: Int) : Response<List<TherapistResponse>>
}