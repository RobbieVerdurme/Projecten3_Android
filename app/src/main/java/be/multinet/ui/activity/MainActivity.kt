package be.multinet.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import be.multinet.R

/**
 * This [AppCompatActivity] is the main Activity for the app.
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //Setup the toolbar, we need it throughout the app.
        setSupportActionBar(findViewById(R.id.mainActivityToolbar))
        //We need to hide the action bar in the splash screen.
        supportActionBar?.hide()
    }
}
