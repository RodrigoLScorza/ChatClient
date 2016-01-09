package fi.toga.chatclient;

import android.os.Handler;
import android.os.Message;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Toga on 15.9.2015.
 */
public class ReceiveMessage implements Runnable {

    private Handler handler;
    private Socket socket;
    private BufferedReader in;
    private String ip;
    private int port;
    private boolean run = true;

    public ReceiveMessage(Socket socket, Handler handler, String ip, int port) {
        this.handler = handler;
        this.socket = socket;
        this.ip = ip;
        this.port = port;
    }
    public void stop() {
        this.run = false;
        System.out.println("ReceiveMessage stop() method");
    }
    @Override
    public void run() {
        try {
            this.socket.connect(new InetSocketAddress(ip, port));
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while(run) {
                String msg = in.readLine();

                Message message = this.handler.obtainMessage();
                message.obj = msg;
                message.what = 0;
                handler.sendMessage(message);
            }
        } catch (Exception e) {
            System.out.println("Receiver broken");
        }
        System.out.println("Receiver finished");
    }
}
