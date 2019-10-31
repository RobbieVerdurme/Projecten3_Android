package be.multinet.model
/**
 * This class represents a Category within the app.
 * @property categoryId the categories ID
 * @property name the categories name
 */
class Category(
    private val categoryId: String,
    private val name: String
) {
    /**
     * @return the id of the category
     */
    fun getCategoryId(): String = categoryId

    /**
     * @return the name of the catgory
     */
    fun getName(): String = name
}