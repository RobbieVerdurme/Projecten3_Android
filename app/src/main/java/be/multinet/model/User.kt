package be.multinet.model

/**
 * This class represents a user within the app.
 * @property userId the user's ID
 * @property surname the user's first name
 * @property familyName the user's family name
 * @property mail the user's e-mail address
 * @property phone the user's phone number
 * @property company the user's company where he/she works
 * @property category the user's different category types
 */
class User (
    private val userId: String,
    private val surname: String,
    private val familyName: String,
    private val mail: String,
    private val phone: String,
    private val category: List<String>
){
    /**
     * @return the id of the user
     */
    fun getUserId(): String = userId

    /**
     * @return the surname of the user
     */
    fun getSurname(): String = surname

    /**
     * @return the family name of the user
     */
    fun getFamilyName(): String = familyName

    /**
     * @return the e-mail address of the user
     */
    fun getMail(): String = mail

    /**
     * @return the phone number of the user
     */
    fun getPhone(): String = phone

    /**
     * @return the different categories of the user
     */
    fun getCategory(): List<String> = category
}