package be.multinet.network.Request

import java.util.*

class CompleteChallengeRequestBody(
    val challengeID:Int,
    val userID:Int,
    val rating:Int,
    val feedback:String,
    val completedOn: Date
)