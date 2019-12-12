package be.multinet.model

import java.util.*

/**
 * This class represents a Challenge within the app.
 * @property challengeId the challenge ID
 * @property name the challenge name
 */
class Challenge(
    private val challengeId: String,
    private val image : String,
    private val title: String,
    private val description:String,
    private var completedDate: Date?,
    private var category: Category?,
    private var rating: Int,
    private var feedback:String?
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
     * @return the rating of the challenge(amount of stars)
     */
    fun getRating(): Int = rating

    /**
     * @return the feedback of the challenge
     */
    fun getFeedback(): String? = feedback

    /**
     * set the challenge on completed
     */
    fun setDateCompleted(dateCompleted:Date){
        completedDate = dateCompleted
    }

    /**
     * set the challenge category
     */
    fun setCategory(newCategory: Category){
        category = newCategory
    }

    /**
     * set the rating of a challenge
     */
    fun setRating(newRating: Int){
        rating = newRating
    }

    /**
     * set the feedback of a challenge
     */
    fun setFeedback(newFeedback: String){
        feedback = newFeedback
    }
}