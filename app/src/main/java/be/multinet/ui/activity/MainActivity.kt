package be.multinet.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import be.multinet.R

/**
 * This [AppCompatActivity] is the main Activity for the app.
 */
class MainActivity : AppCompatActivity() {

    /**
     * Set the layout file.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
