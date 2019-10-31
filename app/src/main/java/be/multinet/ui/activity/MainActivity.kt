package be.multinet.ui.activity

import android.content.BroadcastReceiver
import android.content.IntentFilter
import android.net.wifi.WifiManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import be.multinet.R
import be.multinet.intent.NetworkBroadcastReceiver
import be.multinet.viewmodel.NetworkViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * This [AppCompatActivity] is the main Activity for the app.
 */
class MainActivity : AppCompatActivity() {

    /**
     * The [NetworkViewModel] that will monitor the network
     */
    val network: NetworkViewModel by viewModel()


    /**
     * A list of [BroadcastReceiver]s that might be (un)registered
     */
    private val broadcastReceivers: List<Pair<BroadcastReceiver, IntentFilter>> = ArrayList()

    /**
     * Setup the network change [BroadcastReceiver]
     */
    private fun setupNetworkBroadcastReceiver()
    {
        val networkFilter = IntentFilter()
        networkFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION)
        (broadcastReceivers as ArrayList).add(Pair(NetworkBroadcastReceiver(network),networkFilter))
    }

    /**
     * Register all [BroadcastReceiver]s
     */
    private fun registerReceivers()
    {
        for(pair in broadcastReceivers)
        {
            registerReceiver(pair.first,pair.second)
        }
    }

    /**
     * Unregister all [BroadcastReceiver]s
     */
    private fun unregisterReceivers()
    {
        for(pair in broadcastReceivers)
        {
            unregisterReceiver(pair.first)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //Setup the toolbar, we need it throughout the app.
        setSupportActionBar(this.findViewById(R.id.mainActivityToolbar))
        //We need to hide the action bar in the splash screen.
        supportActionBar?.hide()
        setupNetworkBroadcastReceiver()
    }

    override fun onResume() {
        super.onResume()
        registerReceivers()
    }

    override fun onPause() {
        super.onPause()
        unregisterReceivers()
    }
}
