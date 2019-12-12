package be.multinet.network.Response

import be.multinet.model.Category
import java.util.*

class ChallengeResponse(
    val challengeId: Int,
    val ChallengeImage : String?,
    val title: String,
    val description:String,
    val completedDate: Date?,
    val category: CategoryResponse,
    val rating:Int,
    val feedback:String?
) {
}