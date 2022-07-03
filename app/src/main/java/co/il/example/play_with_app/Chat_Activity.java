package co.il.example.play_with_app;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import co.il.example.play_with_app.SocketTask;

public class Chat_Activity extends AppCompatActivity implements View.OnClickListener {

    TextView tvName;
    EditText etMessage;
    Button btnSend;
    String name;
    LinearLayout linearLayout;
    Client client;
    String messages;
    Handler handler = new Handler();
    Runnable runnable;

    TextView tvConverstion;

    SharedPreferences sp;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        // open chat history
        sp = getSharedPreferences("chat_history", 0);

        tvName = (TextView) findViewById(R.id.tvName);
        // display the name of person you talk with
        Intent intent = getIntent();
        name = intent.getExtras().getString("Username");
        tvName.setText(name);
        messages = sp.getString("Messages_" + name, "");
        etMessage = (EditText) findViewById(R.id.etInput);
        btnSend = (Button) findViewById(R.id.btnMessage);
        btnSend.setOnClickListener(this);

        // calling the action bar
        ActionBar actionBar = getSupportActionBar();
        // Customize the back button
        actionBar.setHomeAsUpIndicator(R.drawable.backbutton);
        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);
        linearLayout = (LinearLayout) findViewById(R.id.linear);
        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy gfgPolicy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(gfgPolicy);
            if (sendData())
                client = new Client();
        }
        if (!messages.equals("")){
            String[] arr = messages.split("@");
            System.out.println(Arrays.toString(arr));
            // display messages my send and receive
            for (int i = 0;i < arr.length;i++){
                char mode = arr[i].charAt(arr[i].length() - 1);
                if (mode == '0'){
                    displayMessage(arr[i].substring(0, arr[i].length() - 1));
                }
                if (mode == '1'){
                    displayReceivedMessage(arr[i].substring(0, arr[i].length() - 1));
                }
            }
        }

    }

    @Override
    protected void onResume() {
        client.receive();
        super.onResume();
    }

    // this event will enable the back
    // function to the button on press
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(Chat_Activity.this, Tabs_Activity.class);
                startActivity(intent);
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        client.sendMessage("stop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        client.sendMessage("stop");
    }

    @Override
    public void onClick(View view) {
        if (view == btnSend) {
            String message = etMessage.getText().toString();
            client.sendMessage(message);
            displayMessage(message);
        }
    }




    @SuppressLint("SetTextI18n")
    public void displayMessage(String message) {
        //add android message to conversion
        tvConverstion = new TextView(this);
        tvConverstion.setBackground(ContextCompat.getDrawable(this, R.drawable.message));
        tvConverstion.setTextSize(20);
        tvConverstion.setGravity(Gravity.LEFT);
        tvConverstion.setTextColor(Color.BLACK);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tvConverstion.setPadding(20, 10, 20, 10);
        layoutParams.setMargins(15, 15, 15, 15);
        tvConverstion.setLayoutParams(layoutParams);
        tvConverstion.setText(message);
        linearLayout.addView(tvConverstion);
        etMessage.setText("");
    }

    @SuppressLint({"RtlHardcoded", "SetTextI18n"})
    public void displayReceivedMessage (String message){
        tvConverstion = new TextView(Chat_Activity.this);
        tvConverstion.setBackground(ContextCompat.getDrawable(this, R.drawable.message2));
        tvConverstion.setTextSize(20);
        tvConverstion.setPadding(20, 10, 20, 10);
        tvConverstion.setTextColor(Color.BLACK);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(15, 15, 15, 15);
        layoutParams.gravity = Gravity.RIGHT;
        tvConverstion.setLayoutParams(layoutParams);
        tvConverstion.setText(message);
        linearLayout.addView(tvConverstion);
    }

    private boolean sendData()
    {
        try {
            String strToSend = "start";
            SocketTask send1 = new SocketTask(strToSend);
            String strReceived = send1.execute().get();
            System.out.println(strReceived);
            if (strReceived.equals("true"))
                return true;
        }
        catch (ExecutionException | InterruptedException e)
        {
            Log.e("Exception", e.toString());
        }
        return false;
    }

    public class Client {

        private Socket socket;
        private BufferedReader bufferedReader;
        private BufferedWriter bufferedWriter;



        public Client(){
            try {
                this.socket = new Socket("X.X.X.X",9860);
                this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            }
            catch (IOException e){
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }

        public void sendMessage (String messageToSend){
            try {
                if (!messageToSend.equals("")) {
                    System.out.println(messageToSend);
                    bufferedWriter.write(messageToSend);
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                    if (!messageToSend.equals("stop")) {
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("Messages_" + name, sp.getString("Messages_" + name, "") + messageToSend + "0" + "@");

                        editor.apply();
                    }
                }
                else {
                    Toast.makeText(Chat_Activity.this, "empty", Toast.LENGTH_LONG).show();
                }
            }
            catch (IOException e){
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }

        public void receive (){
            handler.postDelayed(runnable = new Runnable() {
                @Override
                public void run() {
                    handler.postDelayed(runnable, 2000);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String msgFromChat = "";
                                System.out.println(bufferedReader.ready());
                                if (bufferedReader.ready()) {
                                    msgFromChat = bufferedReader.readLine();
                                    if (!msgFromChat.equals("") || !(msgFromChat == null)) {
                                        displayReceivedMessage(msgFromChat);
                                        SharedPreferences.Editor editor = sp.edit();
                                        editor.putString("Messages_" + name, sp.getString("Messages_" + name, "") + msgFromChat + "1" + "@");
                                        editor.apply();
                                    }
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }, 2000);

        }



        public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){
            try {
                if (bufferedReader != null){
                    bufferedReader.close();
                }
                if (bufferedWriter != null){
                    bufferedWriter.close();
                }
                if (socket != null){
                    socket.close();
                }
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}