package be.multinet.model

import java.util.*

/**
 * This class represents a user within the app.
 * @property userId the user's ID
 * @property surname the user's first name
 * @property familyName the user's family name
 * @property mail the user's e-mail address
 * @property phone the user's phone number
 * @property challenges the challenges of the user
 * @property therapist the user's different therapists
 * @property category the user's different category types
 */
class User (
    private val userId: String,
    private var surname: String,
    private var familyName: String,
    private var mail: String,
    private var phone: String,
    private var category: List<Category> = listOf<Category>(),
    private var therapist: List<Therapist> = listOf<Therapist>(
        Therapist(therapistId = 1, name = "Jan", familyName = "Verstraete", mail = "jan.verstraete@hotmail.com", number = ""),
        Therapist(therapistId = 2, name = "Piet", familyName = "Huysentruyt", mail = "piet.Huysentruyt@hotmail.com", number = ""),
        Therapist(therapistId = 3, name = "Klaas", familyName = "De Muynck", mail = "klaas.de.muynck@hotmail.com", number = ""),
        Therapist(therapistId = 4, name = "Max", familyName = "Van Belle", mail = "max.vanbelle@hotmail.com", number = "")
    ),
    private var challenges: List<Challenge> = listOf<Challenge>(
        Challenge("1","", "Lopen","Loop vandaag 5 km",  null),
        Challenge("2","","Rustdag","Rust vandaag lekker even uit",null),
        Challenge("3","","Gezonde maaltijd","Eet een gezond gerechtje",  Date(2019,11,1 )),
        Challenge("4","","Yoga","Doe de ezelsbrug stand van in de joga", Date(2019,10,31))
    )
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
     * @return the different challenges of the user
     */
    fun getChallenges(): List<Challenge> = challenges

    /**
     * @return the different therapists of the user
     */
    fun getTherapist(): List<Therapist> = therapist

    /**
     * @return the different categories of the user
     */
    fun getCategory(): List<Category> = category

    //setters
    /**
     * @return the surname of the user
     */
    fun setSurname(firstname: String){
        surname = firstname
    }

    /**
     * @return the family name of the user
     */
    fun setFamilyName(familyname: String){
        familyName = familyname
    }

    /**
     * @return the e-mail address of the user
     */
    fun setMail(email: String){
        mail = email
    }

    /**
     * @return the phone number of the user
     */
    fun setPhone(phoneNumber:String){
        phone = phoneNumber
    }

    /**
     * @return the different challenges of the user
     */
    fun setChallenges(challengeList : List<Challenge>){
        challenges = challengeList
    }

    /**
     * @return the different therapists of the user
     */
    fun setTherapist(therapistList: List<Therapist>){
        therapist = therapistList
    }

    /**
     * @return the different categories of the user
     */
    fun setCategory(categoryList: List<Category>){
        category = categoryList
    }
}