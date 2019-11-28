package be.multinet.network.Response

import be.multinet.model.Challenge
import java.util.*

class UserChallengeResponse(
    val userId: Int,
    val userFirstName: String,
    val challenge: ChallengeResponse,
    val completedDate: Date?
) {
}