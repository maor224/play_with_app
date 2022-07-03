package co.il.example.play_with_app;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.text.format.Formatter;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class SocketTask extends AsyncTask<String, Void, String> {

    private final static String IP_ADDRESS = "X.X.X.X";
    private final static int PORT = 11458;
    private Socket socket;
    private String sendingStr="";
    private String receivingStr="";
    BufferedReader reader;

    public SocketTask(String str1)
    {
        this.sendingStr = str1;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected String doInBackground(String... strings) {
        try {
            // create socket
            this.socket = new Socket(IP_ADDRESS, PORT);
            // send request to server ans receive response
            send(Rsa.doEnc(this.sendingStr));
            receive();
            this.socket.close();
        }
        catch (Exception e) {
            Log.e("Exception", e.toString());
        }
        return this.receivingStr;
    }

    // send request to server
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void send(String sendingStr)
    {
        try {
            OutputStreamWriter writer = new OutputStreamWriter(this.socket.getOutputStream(), StandardCharsets.UTF_8);// outputStreamWriter creating
            writer.write(sendingStr);
            System.out.println(sendingStr);
            writer.flush();
            Log.d("Result", "sent");
        }
        catch (Exception e) {
            Log.e("Exception", e.toString());
        }
    }

    // receive response from server
    private void receive()
    {
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            receivingStr = reader.readLine();
            receivingStr = Rsa.doDec(receivingStr);
            reader.close();
        }
        catch (IOException e)
        {
            Log.e("Exception", e.toString());
        }
    }



}
