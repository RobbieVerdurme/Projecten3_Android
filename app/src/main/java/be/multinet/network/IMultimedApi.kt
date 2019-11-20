package be.multinet.network

import be.multinet.model.Therapist
import be.multinet.network.Request.LoginRequestBody
import be.multinet.network.Response.UserChallengeResponse
import be.multinet.network.Response.UserDataResponse
import com.auth0.android.jwt.JWT
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**

 * The interface which provides methods to get result of the Multimed webservice

 */
interface IMultimedApi {
    /**
     * login a user
     */
    @POST("Account")
    suspend fun loginUser(@Body userbody: LoginRequestBody) : Response<String>

    /**
     * get data from a user
     */
    @GET("Users/{id}")
    suspend fun getUser(@Path("id") userid: Int) : Response<UserDataResponse>

    /**
     * get the list of challenges from the user
     */
    @GET("Challenge/user/{id}")
    suspend fun getChallengesUser(@Path("id") userid: Int) : Response<List<UserChallengeResponse>>

    /**
     * update the challenge to complete
     *
    @POST("Challenge/{id}/{id2}")
    suspend fun completeChallenge(@Path("id")userid: Int,@Path("id2") challengeId:Int) : Response<>
    */

    /**
     * get the list of challenges from the user
     */
    @GET("therapist/user/{id}")
    suspend fun getTherapists(@Path("id") userid: Int) : Response<List<Therapist>>
}