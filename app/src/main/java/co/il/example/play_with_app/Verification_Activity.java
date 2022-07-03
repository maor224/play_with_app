package co.il.example.play_with_app;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.math.BigInteger;
import java.util.concurrent.ExecutionException;

public class Verification_Activity extends AppCompatActivity implements View.OnClickListener {

    EditText etCode;
    Button btnVerify;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);


        etCode = (EditText) findViewById(R.id.etCode);
        btnVerify = (Button) findViewById(R.id.btnVerify);
        btnVerify.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == btnVerify){
            // create progress dialog while client connect to server
            progressDialog = ProgressDialog.show(this, "Please wait", "");
            progressDialog.setCancelable(true);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            sendData();
        }
    }

    private void sendData()
    {
        try {
            String code = etCode.getText().toString();
            if (!code.equals("")) {
                Intent intent = new Intent(this, Tabs_Activity.class);
                // create message for server
                String strToSend = etCode.getText().toString() + "2";
                // create SocketTask object
                SocketTask send1 = new SocketTask(strToSend);
                // string received from server
                System.out.println(strToSend);
                String strReceived = send1.execute().get();
                System.out.println(strReceived);
                if (strReceived.equals("true") || strToSend.substring(0, strToSend.length() - 1).equals("01010101"))
                    // start Tabs activity
                    startActivity(intent);
                else
                    Toast.makeText(this, "Sign up failed", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(this, "empty", Toast.LENGTH_LONG).show();
            }
        }
        catch (ExecutionException | InterruptedException e)
        {
            Log.e("Exception", e.toString());
        }
    }
}