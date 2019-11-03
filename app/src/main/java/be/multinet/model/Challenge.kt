package be.multinet.model

/**
 * This class represents a Challenge within the app.
 * @property challengeId the challenge ID
 * @property name the challenge name
 */
class Challenge(
    private val challengeId: String,
    private val image : String,
    private val title: String,
    private val description:String
) {
    /**
     * @return the id of the challenge
     */
    fun getChallengeId(): String = challengeId

    /**
     * @return the image of the challenge
     */
    fun getImage(): String = image

    /**
     * @return the title of the challenge
     */
    fun getTitle(): String = title

    /**
     * @return the description of the challenge
     */
    fun getDescription(): String = description
}