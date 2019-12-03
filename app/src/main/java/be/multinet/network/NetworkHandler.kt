package be.multinet.network

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkInfo
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class NetworkHandler {

    companion object {
        /**
         * The network state
         */
        private var networkState = MutableLiveData<ConnectionState>(ConnectionState.UNAVAILABLE)

        @RequiresApi(Build.VERSION_CODES.N)
        val networkCallback: ConnectivityManager.NetworkCallback = AppNetworkCallback()

        fun getNetworkState(): MutableLiveData<ConnectionState> = networkState

        fun onNetworkAvailable(){
            //MutableLiveData can only be updated on Main thread.
            val runnable = Runnable {
                networkState.value = ConnectionState.CONNECTED
                Log.d("network","Connected")
            }
            Handler(Looper.getMainLooper()).post(runnable)
        }

        fun onNetworkUnavailable(){
            //MutableLiveData can only be updated on Main thread.
            val runnable = Runnable {
                networkState.value = ConnectionState.DISCONNECTED
                Log.d("network","Disconnected")
            }
            Handler(Looper.getMainLooper()).post(runnable)
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private class AppNetworkCallback : ConnectivityManager.NetworkCallback(){
        override fun onAvailable(network: Network) = onNetworkAvailable()
        override fun onLost(network: Network) = onNetworkUnavailable()
    }
}

/**
 * This [BroadcastReceiver] listens for network changes.
 * It only processes instructions for API < 24, since the new API uses [ConnectivityManager.NetworkCallback].
 */
class NetworkBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        //Check API level, CONNECTIVITY_ACTION is deprecated in API 28, but we already switch to NetworkCallback in API 24, hence the API 24 check.
        //NetworkInfo is also deprecated since API 29, but we already have an API check so it's ok.
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.N && intent?.action!! == ConnectivityManager.CONNECTIVITY_ACTION){
            val connMan = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork: NetworkInfo? = connMan.activeNetworkInfo
            if(activeNetwork?.isConnectedOrConnecting == true){
                NetworkHandler.onNetworkAvailable()
            }else{
                NetworkHandler.onNetworkUnavailable()
            }
        }
    }
}

