package be.multinet.model

public class LeaderboardUser (
    private val name: String,
    private val score: Int
){
    fun getName():String = name
    fun getScore():Int = score
}

