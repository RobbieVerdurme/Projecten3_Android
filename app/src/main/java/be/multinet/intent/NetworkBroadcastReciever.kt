package be.multinet.intent

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.NetworkInfo
import android.net.wifi.WifiManager
import be.multinet.network.ConnectionState
import be.multinet.viewmodel.NetworkViewModel

/**
 * This [BroadcastReceiver] monitors network changes and notifies [networkViewModel]
 * @param networkViewModel the viewmodel to notify of network changes
 */
class NetworkBroadcastReceiver(private val networkViewModel: NetworkViewModel) : BroadcastReceiver() {

    private val connection = WifiManager.NETWORK_STATE_CHANGED_ACTION

    /**
     * Notify the viewmodel if the network changes
     * @param context the context that sent this [Intent]
     * @param intent the network changed [Intent]
     */
    override fun onReceive(context: Context?, intent: Intent?) {
        if(intent?.action == connection)
        {
            val currentNetwork: NetworkInfo? = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO)
            networkViewModel.setNetworkState(if(currentNetwork == null || !currentNetwork.isConnected) ConnectionState.DISCONNECTED else ConnectionState.CONNECTED)
        }
    }
}