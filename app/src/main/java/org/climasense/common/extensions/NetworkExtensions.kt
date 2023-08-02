package org.climasense.common.extensions

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.core.content.getSystemService

/**
 * Taken from Tachiyomi
 * Apache License, Version 2.0
 *
 * https://github.com/tachiyomiorg/tachiyomi/blob/d703fb79462574c96c5c429c83d0c3d44de99082/app/src/main/java/eu/kanade/tachiyomi/util/system/NetworkExtensions.kt
 */
val Context.connectivityManager: ConnectivityManager
    get() = getSystemService()!!

fun Context.isOnline(): Boolean {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        val maxTransport = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1 -> NetworkCapabilities.TRANSPORT_LOWPAN
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> NetworkCapabilities.TRANSPORT_WIFI_AWARE
            else -> NetworkCapabilities.TRANSPORT_VPN
        }
        return (NetworkCapabilities.TRANSPORT_CELLULAR..maxTransport).any(networkCapabilities::hasTransport)
    } else {
        @Suppress("DEPRECATION")
        return connectivityManager.activeNetworkInfo?.isConnected ?: false
    }
}