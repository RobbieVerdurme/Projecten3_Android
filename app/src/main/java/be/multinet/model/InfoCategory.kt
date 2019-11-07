package be.multinet.model

import android.icu.text.CaseMap

class InfoCategory(
    private val subCategoryTitle: String,
    private val items: List<Info>
) {
    fun getSubCategoryTitle() = subCategoryTitle

    fun getCategoryItems() = items
}