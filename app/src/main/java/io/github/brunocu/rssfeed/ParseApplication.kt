package io.github.brunocu.rssfeed

import android.util.Log

class ParseApplication {
    private val TAG = "ParseApplication"
    val applications = ArrayList<FeedEntry>()

    fun parse(xmlData: String): Boolean {
        Log.d(TAG, "parse + ${xmlData.length}")
        return false
    }
}