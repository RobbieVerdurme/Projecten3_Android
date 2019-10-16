package be.multinet.model

/**
 * This class represents a company within the app.
 * @property companyId the company's ID
 * @property name the name of the company
 */
class Company(
    private val companyId: String,
    private val name: String
) {

    /**
     * @return the id of the company
     */
    fun getCompanyId(): String = companyId

    /**
     * @return the name of the company
     */
    fun getName(): String = name
}