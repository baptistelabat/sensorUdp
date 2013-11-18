package jp.ac.ehime_u.cite.sasaki.SensorUdp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import android.util.Log;

public class SenderThread extends Thread {

    String destinationHost;
    int destinationPort;
    DatagramSocket datagramSocket;
    static SenderThread senderThread;
    static boolean inGetSingleton;

    SenderThread() throws SocketException {
        datagramSocket = new DatagramSocket();
    }

    synchronized public static SenderThread GetSingleton(
            String destination_host, int destination_port)
            throws SocketException {
        inGetSingleton = true;
        if (senderThread == null) {
            senderThread = new SenderThread();
        }
        senderThread.destinationHost = destination_host;
        senderThread.destinationPort = destination_port;
        inGetSingleton = false;
        return senderThread;
    }

    public void Queue(String line) {
        // TODO:
    }

    public void run() {

    }

    public void SendMessageByUdp(String string_to_be_sent) {
        try {
            byte[] byte_array = string_to_be_sent.getBytes();
            InetAddress inet_address = InetAddress.getByName(destinationHost);
            DatagramPacket datagram_packet = new DatagramPacket(byte_array,
                    byte_array.length, inet_address, destinationPort);
            // DatagramSocket datagram_socket = new DatagramSocket();
            if (null == datagramSocket) {
                // datagramSocket.close();
                // datagramSocket = null;
                datagramSocket = new DatagramSocket();
            }
            datagramSocket.send(datagram_packet);
        } catch (IOException io_exception) {
            datagramSocket = null;
            Log.v("SensorUdp#SendDebugMessageByUdp", io_exception.toString());
        }
    }

}
