package be.multinet.network.Response

import be.multinet.model.Category
import retrofit2.http.Field

class UserDataResponse(
    @Field("userId")
    val userId: String,
    @Field("firstName")
    val surname: String,
    @Field("familyName")
    val familyName: String,
    @Field("email")
    val mail: String,
    @Field("phone")
    val phone: String,
    @Field("categories")
    val category: List<Category>
) {
}