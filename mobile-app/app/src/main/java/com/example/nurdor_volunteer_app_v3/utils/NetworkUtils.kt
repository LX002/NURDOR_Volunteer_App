package com.example.nurdor_volunteer_app_v3.utils

import java.net.DatagramSocket
import java.net.InetAddress

object NetworkUtils {
    fun getIPV4(): String? {
        return DatagramSocket().use { socket ->
            socket.connect(InetAddress.getByName("8.8.8.8"), 12345)
            socket.localAddress.hostAddress
        }
    }
}