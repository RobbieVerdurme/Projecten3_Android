package be.multinet.model

/**
 * This enum declares the different login related states for a user.
 * [UNKNOWN] When the state wasn't checked yet, so we don't know if the user is present or not.
 * [LOGGED_IN] The user was checked and is present.
 * [LOGGED_OUT] The user was checked but isn't present.
 */
enum class UserLoginState {
    UNKNOWN,LOGGED_OUT,LOGGED_IN
}