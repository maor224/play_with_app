package co.il.example.play_with_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.math.BigInteger;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

public class SettingsActivity extends AppCompatActivity {

    EditText etUpdateEmail, etUpdatePassword;
    Button btnUpdate;

    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    SharedPreferences sp;
    String email, password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        etUpdateEmail = (EditText) findViewById(R.id.updateEmail);
        etUpdatePassword = (EditText) findViewById(R.id.updatePassword);
        btnUpdate = (Button) findViewById(R.id.btnUpdate);

        sp = getSharedPreferences("savedData", 0);
        email = sp.getString("Email", "");
        password = sp.getString("Pass", "");

        //init bio metric
        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(SettingsActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                //error authenticating, stop tasks that requires auth
                Toast.makeText(SettingsActivity.this, "Authentication error: " + errString, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                //authentication succeed, continue tasts that requires auth
                sendData();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                //failed authenticating, stop tasks that requires auth
                Toast.makeText(SettingsActivity.this, "Authentication failed...!", Toast.LENGTH_SHORT).show();
            }
        });

        //setup title,description on auth dialog
        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric Authentication")
                .setSubtitle("Identification using fingerprint authentication")
                .setNegativeButtonText("disagree")
                .build();

        //handle btnUpdate click, start authentication if it is enabled
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 27) {
                    //show auth dialog
                    biometricPrompt.authenticate(promptInfo);
                }
                else {
                    sendData();
                }

            }
        });
    }

    private void sendData()
    {
        try {
            Intent intent = new Intent(this, Tabs_Activity.class);
            String strToSend = "", uEmail, uPass;
            uEmail = etUpdateEmail.getText().toString();
            uPass = etUpdatePassword.getText().toString();
            // check if email edittext is not empty and do the update
            if (!uEmail.equals("")){
                strToSend += email + ",";
                strToSend += uEmail + "5" + "/551:17";
                String[] arrRSA = strToSend.split("/");
                String publickey = arrRSA[1];
                Rsa.n = new BigInteger(String.valueOf(publickey.substring(0, 3)));
                Rsa.e = new BigInteger(String.valueOf(publickey.substring(4, 6)));
                SocketTask send1 = new SocketTask(arrRSA[0]);
                System.out.println(arrRSA[0]);
                String strReceived = send1.execute().get();
                System.out.println(strReceived);
                if (strReceived.equals("true")) {
                    // replace the email with the new one
                    @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = sp.edit();
                    editor.putString("Email", uEmail);
                    editor.apply();
                    Toast.makeText(this, "Update Succeeded", Toast.LENGTH_LONG).show();
                    // start tabs activity
                    startActivity(intent);
                }
                else
                    Toast.makeText(this, "Update failed", Toast.LENGTH_LONG).show();
            }
            // check if password edittext is not empty and do the update
            if (!uPass.equals("")){
                strToSend += password + ",";
                strToSend += uPass + "6";
                SocketTask send1 = new SocketTask(strToSend);
                System.out.println(strToSend);
                String strReceived = send1.execute().get();
                System.out.println(strReceived);
                if (strReceived.equals("true")) {
                    // replace the password with the new one
                    @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = sp.edit();
                    editor.putString("Pass", uPass);
                    editor.apply();
                    Toast.makeText(this, "Update Succeeded", Toast.LENGTH_LONG).show();
                    // start tabs activity
                    startActivity(intent);
                }
                else
                    Toast.makeText(this, "Update failed", Toast.LENGTH_LONG).show();
            }
            if (etUpdateEmail.getText().toString().equals("") &&
                    etUpdatePassword.getText().toString().equals("")){
                Toast.makeText(this, "empty", Toast.LENGTH_LONG).show();
            }
        }
        catch (ExecutionException | InterruptedException e)
        {
            Log.e("Exception", e.toString());
        }
    }


}