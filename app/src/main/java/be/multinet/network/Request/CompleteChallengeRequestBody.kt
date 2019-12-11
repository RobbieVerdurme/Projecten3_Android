package be.multinet.network.Request

class CompleteChallengeRequestBody(
    var challengeID:Int,
    var userID:Int,
    var rating:Int,
    var feedback:String
) {
}