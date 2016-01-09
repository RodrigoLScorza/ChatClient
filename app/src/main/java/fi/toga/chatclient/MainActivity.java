package fi.toga.chatclient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {

    private EditText nick;
    private EditText ip;
    private EditText port;
    private String PATTERN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nick = (EditText)findViewById(R.id.nick);
        ip = (EditText)findViewById(R.id.ipaddress);
        port = (EditText)findViewById(R.id.port);
        PATTERN = "^((0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)\\.){3}(0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)$";
    }
    public void connect(View view) {
        if (!nick.getText().toString().equals("") && !ip.getText().toString().equals("") && !port.getText().toString().equals("") && ip.getText().toString().matches(PATTERN)) {
            try {
                Intent chat = new Intent();
                chat.setClass(getBaseContext(), Chat.class);
                chat.putExtra("nick", nick.getText().toString());
                chat.putExtra("ip", ip.getText().toString());
                chat.putExtra("port", Integer.parseInt(port.getText().toString()));
                startActivity(chat);
                finish();
            } catch (Exception e) {
                Toast.makeText(this, R.string.check_your_fields, Toast.LENGTH_SHORT).show();
                System.out.println(e);
            }
        } else {
            Toast.makeText(this, R.string.fill_all, Toast.LENGTH_SHORT).show();
        }

    }
}
