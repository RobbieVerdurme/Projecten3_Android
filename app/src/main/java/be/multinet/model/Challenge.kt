package be.multinet.model

import java.util.*

/**
 * This class represents a Challenge within the app.
 */
class Challenge(
    private val challengeId: String,
    private val image : String,
    private val title: String,
    private val description:String,
    private var completedDate: Date?,
    private var category: Category?
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

    /**
     * @return true if the challenge is completed
     */
    fun getDateCompleted(): Date? = completedDate

    /**
     * @return the category
     */
    fun getCategory(): Category? = category

    /**
     * set the challenge on completed
     */
    fun setDateCompleted(dateCompleted:Date){
        completedDate = dateCompleted
    }
}