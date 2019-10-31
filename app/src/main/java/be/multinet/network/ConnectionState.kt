package be.multinet.network

    /**
     * This [Enum] declares the different states for a wireless connection
     */
    enum class ConnectionState {
        /**
         * wifi is available and connected
         */
        CONNECTED,
        /**
         * wifi is available but disconnected
         */
        DISCONNECTED,
        /**
         * wifi is unavailable
         */
        UNAVAILABLE
    }