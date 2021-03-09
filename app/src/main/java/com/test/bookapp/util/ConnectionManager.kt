package com.test.bookapp.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

class ConnectionManager {

    fun checkConnection(context: Context): Boolean {

        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        var activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo

        return if ( activeNetwork?.isConnected != null) {
            activeNetwork.isConnected
        }else{
            false
        }

    }

}