package be.multinet.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import be.multinet.R
import be.multinet.network.ConnectionState
import be.multinet.network.ConnectionState.*

class NetworkViewModel constructor(application: Application): AndroidViewModel(application) {
    /**
     * The network state
     */
    private val networkState = MutableLiveData<ConnectionState>()

    /**
     * The network state message
     */
    private val networkStateMessage = MutableLiveData<String>()

    /**
     * Enable wifi dialog title
     */
    val enableWifiDialogTitle: String= application.getString(R.string.dialog_enable_wireless_title)

    /**
     * Enable wifi dialog 'Ok' button text
     */
    val enableWifiDialogContinue: Int = R.string.dialog_enable_wireless_continue
    /**
     * Enable wifi dialog 'Cancel' button text
     */
    val enableWifiDialogCancel: Int = R.string.dialog_cancel
    /**
     * Enable wifi dialog message
     */
    val enableWifiDialogDescription: Int = R.string.dialog_enable_wireless_description
    /**
     * 'Offline' message
     */
    private val offline: String = application.getString(R.string.offline)
    /**
     * 'Network unavailable' message
     */
    private val networkUnavailable: String = application.getString(R.string.network_unavailable)

    /**
     * Initialize the values
     */
    init {
        networkState.value = ConnectionState.UNAVAILABLE
        networkStateMessage.value = ""
    }

    /**
     * Get [networkStateMessage]
     * @return [LiveData] of [networkStateMessage]
     */
    fun getNetworkStateMessage(): LiveData<String> = networkStateMessage

    /**
     * Get [networkState]
     * @return [LiveData] of [networkState]
     */
    fun getNetworkState(): LiveData<ConnectionState> = networkState

    /**
     * Get the current network state
     * @return the current value of [networkState]
     */
    fun getCurrentNetworkState(): ConnectionState
    {
        return networkState.value!!
    }

    /**
     * Set a new value for [networkState]
     * @param value the new value
     */
    fun setNetworkState(value : ConnectionState)
    {
        if(networkState.value != value)
        {
            networkState.value = value
            processNetworkState()
        }
    }

    /**
     * Process the current value of [networkState] and set a message accordingly
     */
    private fun processNetworkState()
    {
        when(networkState.value){
            CONNECTED -> {
                networkStateMessage.value = ""
            }
            DISCONNECTED -> {
                networkStateMessage.value = offline
            }
            UNAVAILABLE -> {
                networkStateMessage.value = networkUnavailable
            }
        }
    }
}