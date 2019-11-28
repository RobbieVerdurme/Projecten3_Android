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
    private var token:String,
    private var surname: String,
    private var familyName: String,
    private var mail: String,
    private var phone: String,
    private var contract: Date,
    private var category: List<Category> = listOf<Category>(),
    private var exp: Int
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
    fun getCategory(): List<Category> = category

    /**
     * @return the different categories of the user
     */
    fun getToken(): String = token

    /**
     * @return the contract date of the user
     */
    fun getContractDate(): Date = contract

    /**
     * @return the contract date of the user
     */
    fun getEXP(): Int = exp

    //setters
    /**
     * sets the surname of the user
     */
    fun setSurname(firstname: String){
        surname = firstname
    }

    /**
     * sets the family name of the user
     */
    fun setFamilyName(familyname: String){
        familyName = familyname
    }

    /**
     * sets the email of the user
     */
    fun setMail(email: String){
        mail = email
    }

    /**
     * sets the phone of the user
     */
    fun setPhone(phoneNumber:String){
        phone = phoneNumber
    }

    /**
     * sets the therapists of the user
     */
    fun setCategory(categoryList: List<Category>){
        category = categoryList
    }

    /**
     * sets the token of the logged in user
     */
    fun setToken(newToken: String){
        token = newToken
    }

    /**
     * sets the contract date to the new date
     */
    fun setContract(contractDate: Date){
        contract = contractDate
    }

    /**
     * sets the contract date to the new date
     */
    fun setEXP(totalEXP: Int){
        exp = totalEXP
    }
}