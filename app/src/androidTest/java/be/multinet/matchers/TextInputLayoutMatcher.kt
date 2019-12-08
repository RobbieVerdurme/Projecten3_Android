package be.multinet.matchers

import android.view.View
import com.google.android.material.textfield.TextInputLayout
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

/**
 * This [TypeSafeMatcher] helps with testing [TextInputLayout]'s error text.
 */
fun hasTextInputLayoutErrorMessage(expectedErrorText: String?): Matcher<View> = object : TypeSafeMatcher<View>() {

    override fun describeTo(description: Description?) { }

    override fun matchesSafely(item: View?): Boolean {
        if (item !is TextInputLayout) return false
        return expectedErrorText == item.error
    }
}