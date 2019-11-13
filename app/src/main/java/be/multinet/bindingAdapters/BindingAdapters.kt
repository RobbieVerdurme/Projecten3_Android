package be.multinet.bindingAdapters

import com.google.android.material.textfield.TextInputLayout
import androidx.databinding.BindingAdapter

/**
 * A [BindingAdapter] to provide xml fragmentChallengesCategoryBinding for a [TextInputLayout]'s error property
 * @param view the view to bind for
 * @param errorMessage the message to bind
 */
@BindingAdapter("app:error")
fun setErrorMessage(view: TextInputLayout, errorMessage: String?) {
    view.error = errorMessage
}
