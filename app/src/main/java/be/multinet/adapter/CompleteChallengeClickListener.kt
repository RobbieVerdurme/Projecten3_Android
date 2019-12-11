package be.multinet.adapter

import be.multinet.model.Challenge

/**
 * This interface defines a contract to detect clicks for the 'Complete challenge' button in an [Challenge] recyclerview item
 */
interface CompleteChallengeClickListener {
    /**
     * A callback for handling [Challenge] list item selection
     * @param item the selected [Challenge]
     */
    fun onItemClicked(item: Challenge)
}