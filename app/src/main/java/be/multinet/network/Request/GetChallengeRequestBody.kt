package be.multinet.network.Request

import retrofit2.http.Field

class GetChallengeRequestBody(
    @Field("ChallengeId")
    val ChallengeId: Int)
{

}