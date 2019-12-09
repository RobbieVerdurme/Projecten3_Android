package be.multinet.ui.activity

import android.annotation.TargetApi
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkRequest
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import be.multinet.R
import be.multinet.network.NetworkBroadcastReceiver
import be.multinet.network.NetworkHandler
import com.google.android.material.bottomnavigation.BottomNavigationView


/**
 * This [AppCompatActivity] is the main Activity for the app.
 */
class MainActivity : AppCompatActivity() {

    /**
     * Broadcast receiver for API < 24
     */
    private var networkBroadcastReceiver: BroadcastReceiver? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //Setup the toolbar, we need it throughout the app.
        setSupportActionBar(this.findViewById(R.id.mainActivityToolbar))
        //We need to hide the action bar in the splash screen.
        supportActionBar?.hide()
    }

    override fun onResume() {
        super.onResume()
        registerNetworkHandler()
    }

    override fun onPause() {
        super.onPause()
        unregisterNetworkHandler()
    }

    private fun registerNetworkHandler(){
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.N){
            networkBroadcastReceiver = NetworkBroadcastReceiver()
            registerReceiver(networkBroadcastReceiver,IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        }else{
            val connMan = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            connMan.registerNetworkCallback(NetworkRequest.Builder().build(),NetworkHandler.networkCallback)
        }
    }

    private fun unregisterNetworkHandler(){
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.N){
            unregisterReceiver(networkBroadcastReceiver)
        }else{
            val connMan = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            connMan.unregisterNetworkCallback(NetworkHandler.networkCallback)
        }
    }
}
