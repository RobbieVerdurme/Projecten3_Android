package be.multinet.network.Response

import be.multinet.model.Category
import retrofit2.http.Field
import java.util.*

class UserDataResponse(
    val userId: String,
    val firstName: String,
    val familyName: String,
    val email: String,
    val phone: String,
    val contract: Date,
    val categories: List<Category>,
    val experiencePoints:Int
) {
}