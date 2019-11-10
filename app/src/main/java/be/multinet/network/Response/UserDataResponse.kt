package be.multinet.network.Response

import be.multinet.model.Category
import retrofit2.http.Field

class UserDataResponse(
    @Field("userId")
    val userId: String,
    @Field("firstName")
    val firstName: String,
    @Field("familyName")
    val familyName: String,
    @Field("email")
    val email: String,
    @Field("phone")
    val phone: String,
    @Field("categories")
    val categories: List<Category>
) {
}