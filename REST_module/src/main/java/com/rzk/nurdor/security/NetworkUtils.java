package com.rzk.nurdor.security;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class NetworkUtils {

    public static String getIPV4() {
        try (final DatagramSocket datagramSocket = new DatagramSocket()) {
            datagramSocket.connect(InetAddress.getByName("8.8.8.8"), 12345);
            return datagramSocket.getLocalAddress().getHostAddress();
        } catch (SocketException | UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(final String[] args) {
        try {
            System.out.println(NetworkUtils.getIPV4());
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }
}
