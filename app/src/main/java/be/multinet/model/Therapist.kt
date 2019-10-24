package be.multinet.model

/**
 * This class represents a therapist within the app.
 * @property therapistId the therapist's Id
 * @property name the therapist's name
 * @property number the therapist's phone number
 * @property mail the therapist's mail address
 */
class Therapist (
    private val therapistId: Int,
    private val name: String,
    private val number: String,
    private val mail: String
    ){

    /**
     * @return the id of the therapist
     */
    fun getId(): Int = therapistId

    /**
     * @return the name of the therapist
     */
    fun getName(): String = name

    /**
     * @return the id of the therapist
     */
    fun getNumber(): String = number

    /**
     * @return the id of the therapist
     */
    fun getMail(): String = mail

}