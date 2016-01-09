package fi.toga.chatclient;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by Toga on 15.9.2015.
 */
public class SendMessage {
    private Socket socket;
    private PrintWriter out;

    public SendMessage(Socket socket) throws IOException {
        this.socket = socket;
        out = new PrintWriter(socket.getOutputStream(), true);
    }
    public void send(String message) {
        out.println(message);
        out.flush();
    }
}
