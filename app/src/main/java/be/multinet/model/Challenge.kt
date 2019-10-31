package be.multinet.model

/**
 * This class represents a Challenge within the app.
 * @property challengeId the challenge ID
 * @property name the challenge name
 */
class Challenge(
    private val challengeId: String,
    private val name: String
) {
    /**
     * @return the id of the challenge
     */
    fun getChallengeId(): String = challengeId

    /**
     * @return the name of the challenge
     */
    fun getName(): String = name
}