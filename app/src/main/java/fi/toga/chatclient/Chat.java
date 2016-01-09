package fi.toga.chatclient;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by Toga on 17.9.2015.
 */
public class Chat extends AppCompatActivity {
    private ScrollView scrollView;
    private ReceiveMessage rm;
    private SendMessage sm;
    private Intent mainActivity;
    private static String nick;
    private String print;
    private String ip;
    private int port;
    private EditText message;
    private Socket socket = new Socket();
    LinearLayout ll;
    private Handler handler = new Handler() {
        public void handleMessage(Message message) {
            if (message.what == 0) {
                TextView text = new TextView(getApplicationContext());
                print = "" + message.obj;
                text.setText(print);
                text.setTextColor(0xff000000);
                text.setBackgroundColor(0x7700ff00);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.WRAP_CONTENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
                if (!print.contains(":")) {
                    params.gravity = Gravity.CENTER_HORIZONTAL;
                    text.setLayoutParams(params);
                    text.setBackgroundColor(0x7711aa11);
                } else if (print.split(":")[0].equals(nick)) {
                    params.gravity = Gravity.RIGHT;
                    params.setMargins(50, 5, 5, 5);
                    text.setLayoutParams(params);
                    text.setBackgroundColor(0x7700bb11);
                } else {
                    params.setMargins(5,5,50,5);
                    text.setLayoutParams(params);
                }

                ll.addView(text);
            }
            scrollView.post(new Runnable() {
                @Override
                public void run() {
                    scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_layout);
        scrollView = (ScrollView)findViewById(R.id.scrollView);
        ll = (LinearLayout)findViewById(R.id.message_field);
        mainActivity = getIntent();
        nick = mainActivity.getExtras().getString("nick");
        ip = mainActivity.getExtras().getString("ip");
        port = mainActivity.getExtras().getInt("port");
        message = (EditText)findViewById(R.id.send_field);
        Thread t = new Thread(this.rm = new ReceiveMessage(socket, handler, ip, port));
        t.start();
        try {
            Thread.sleep(500);
            sm = new SendMessage(socket);
            sm.send(":name " + nick);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(View view) {
        if (!message.getText().toString().equals("")) {
            if (message.getText().toString().equals(":quit")) {
                quit();
            } else {
                sm.send(message.getText().toString());
                message.setText("");
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_change_user) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle(R.string.change_user_title);
            alert.setMessage(R.string.change_user_message);
            final EditText newNick = new EditText(getApplicationContext());
            newNick.setTextColor(0xff000000);
            alert.setView(newNick);

            alert.setPositiveButton(R.string.change_user_ok, new
                    DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            sm.send(":name " + newNick.getText().toString());
                            Chat.nick = newNick.getText().toString();
                            Toast.makeText(getApplicationContext(), R.string.change_user_toast, Toast.LENGTH_SHORT).show();
                        }
                    });
            alert.setNegativeButton(R.string.change_user_cancel, new
                    DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        }
                    });

            alert.show();
            return true;
        }
        if (id == R.id.menu_quit) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle(R.string.quit_title);
            alert.setMessage(R.string.quit_message);

            alert.setPositiveButton(R.string.quit_ok, new
                    DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            Toast.makeText(getApplicationContext(), R.string.bye, Toast.LENGTH_SHORT).show();
                            quit();
                        }
                    });
            alert.setNegativeButton(R.string.quit_cancel, new
                    DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        }
                    });

            alert.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private void quit() {
        System.out.println("Chat quit() method");
        rm.stop();
        sm.send(":quit");
        try {
            this.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finish();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        quit();
    }
}
