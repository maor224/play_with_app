package co.il.example.play_with_app;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUp_Activity1 extends AppCompatActivity implements View.OnClickListener {

    EditText etEmail, etPass, etPhone;
    Button btnRules, btnContinue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up1);

        etEmail = (EditText) findViewById(R.id.etEmail);
        etPass = (EditText) findViewById(R.id.etPass);
        etPhone = (EditText) findViewById(R.id.etPhone);
        btnRules = (Button) findViewById(R.id.btnRules);
        btnContinue = (Button) findViewById(R.id.btnContinue);
        btnContinue.setOnClickListener(this);
        btnRules.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == btnContinue){
            Intent intent = new Intent(this, SignUp_Activity2.class);
            // check edittexts arent empty
            if (!etEmail.getText().toString().equals("") &&
                    !etPass.getText().toString().equals("") &&
                    !etPhone.getText().toString().equals("")){

                if (check_email(etEmail.getText().toString()) &&
                        check_password(etPass.getText().toString()) &&
                        check_phone(etPhone.getText().toString())) {
                    // pass the email, password and phone to the next screen
                    intent.putExtra("Email", etEmail.getText().toString());
                    intent.putExtra("Pass", etPass.getText().toString());
                    intent.putExtra("Phone", etPhone.getText().toString());
                    startActivity(intent);
                }
                else {
                    Toast.makeText(this, "try again", Toast.LENGTH_LONG).show();
                }
            }
            else {
                Toast.makeText(this, "empty", Toast.LENGTH_LONG).show();
            }
        }
        // password rules dialog
        if (v == btnRules){
            Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.rules_layout);
            dialog.setTitle("Rules for password");
            dialog.setCancelable(true);
            dialog.show();
        }
    }

    private static boolean check_email(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }

    private boolean check_password(String password){
        char[] signs = {'@', '#', '!', '$', '&', '*'};
        String letters = "abcdefghijklmnopqrstuvwxyz";
        String numbers = "1234567890";
        String capital = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        int count = 0, count2 = 0, count3 = 0, count4 = 0;
        if (password.length() >= 8){
            for (int i = 0;i < password.length();i++){
                for (int j = 0;j < signs.length;j++){
                    if (password.charAt(i) == signs[j])
                        count++;
                }
            }
            if (count >= 1) {
                for (int i = 0;i < password.length();i++){
                    for (int j = 0;j < letters.length();j++){
                        if (password.charAt(i) == letters.charAt(j)){
                            count2++;
                        }
                    }

                    for (int j = 0;j < numbers.length();j++){
                        if (password.charAt(i) == numbers.charAt(j)){
                            count3++;
                        }
                    }

                    for (int j = 0;j < capital.length();j++){
                        if (password.charAt(i) == capital.charAt(j)){
                            count4++;
                        }
                    }
                }
                if (count2 + count3 + count4 == password.length() - count &&
                        count2 > 0 && count3 > 0 && count4  > 0)
                    return true;
                else {
                    Toast.makeText(this, "must have letters and " +
                            "numbers and capital letter", Toast.LENGTH_LONG).show();
                }
            }
            else {
                Toast.makeText(this, "must have at least 1 sign", Toast.LENGTH_LONG).show();
            }

        }
        else{
            Toast.makeText(this, "must include at least 8 characters"
                    , Toast.LENGTH_LONG).show();
        }
        return false;
    }

    private boolean check_phone(String phone) {
        // 1) Begins with 0 or 972
        // 2) Then contains 5
        // 3) Then contains 8 digits
        Pattern p = Pattern.compile("(0|972)?[5][0-9]{8}");

        // Pattern class contains matcher() method
        // to find matching between given number
        // and regular expression
        Matcher m = p.matcher(phone);
        return (m.find() && m.group().equals(phone));
    }


}