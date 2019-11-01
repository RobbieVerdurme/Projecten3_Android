package be.multinet.adapter

import androidx.cardview.widget.CardView

interface IChallengeAdapter {
    fun getBaseElevation(): Float
    fun getCardViewAt(position: Int): CardView
    fun getCount() : Int
}