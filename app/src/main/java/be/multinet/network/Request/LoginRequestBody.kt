package be.multinet.network.Request

import retrofit2.http.Field

class LoginRequestBody(
    @Field("username")
    val username: String,
    @Field("password")
    val password: String
) {
}