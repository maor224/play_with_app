package co.il.example.play_with_app;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class SignUp_Activity3 extends AppCompatActivity implements View.OnClickListener {

    EditText etDate;
    Button btnRegister;
    String email, pass, username, game, gender, phone;
    Spinner level, city;
    String levelItem, cityItem;
    Intent intent2;
    SharedPreferences sp;
    String serverStr = "", strMessage = "",strToServer;
    ProgressDialog progressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up3);

        // create shared preference file
        sp = getSharedPreferences("savedData", 0);

        // get email, password, phone, username, gender and game from previous screen
        Intent intent = getIntent();
        email = intent.getExtras().getString("Email");
        pass = intent.getExtras().getString("Pass");
        username = intent.getExtras().getString("Username");
        game = intent.getExtras().getString("Game");
        gender = intent.getExtras().getString("Gender");
        phone = intent.getExtras().getString("Phone");

        city = (Spinner)findViewById(R.id.citiesSpinner);
        level = (Spinner)findViewById(R.id.levelSpinner);
        etDate = (EditText) findViewById(R.id.etDate);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(this);

        // create levels list
        final List<String> levels = Arrays.asList("Beginner", "Intermediate", "Expert");

        // create array adapter and set details for levels
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, levels);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        level.setAdapter(adapter);

        // create cities list
        final List<String> cities = Arrays.asList("Rishon LeZion", "Bat Yam", "Ness Ziona", "Tel Aviv", "Rehovot");

        // create array adapter and set details for cities
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, cities);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        city.setAdapter(adapter2);

        // on item selected for level
        level.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                levelItem = level.getSelectedItem().toString();
                Toast.makeText(getApplicationContext(), "You selected: " + levelItem, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // on item selected for city
        city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cityItem = city.getSelectedItem().toString();
                Toast.makeText(getApplicationContext(), "You selected: " + cityItem, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == btnRegister){
            String date = etDate.getText().toString();
            if (!date.equals("")) {
                if (Calendar.getInstance().get(Calendar.YEAR) - 16 >= Integer.parseInt(date.substring(6, 10)) &&
                        Integer.parseInt(date.substring(6, 10)) > 1952 &&
                        Integer.parseInt(date.substring(0, 2)) >= 1 &&
                        Integer.parseInt(date.substring(0, 2)) <= 31 &&
                        Integer.parseInt(date.substring(3, 5)) >= 1 &&
                        Integer.parseInt(date.substring(3, 5)) <= 12) {
                    intent2 = new Intent(this, Verification_Activity.class);
                    // write to savedData file the details of the user
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("Email", email);
                    editor.putString("Pass", pass);
                    editor.putString("Phone", phone);
                    editor.putString("Username", username);
                    editor.putString("Gender", gender);
                    editor.putString("Game", game);
                    editor.putString("City", cityItem);
                    editor.putString("Level", levelItem);
                    editor.putString("Date", date);
                    editor.apply();
                    // create message for server
                    strMessage = email + "," + pass + "," + phone + "," + username + "," +
                            gender + "," + game + "," + cityItem + "," + levelItem + "," +
                            etDate.getText().toString() + "0";
                    System.out.println(strMessage);
                    // create progress dialog while client connect to server
                    progressDialog = ProgressDialog.show(this, "Please wait", "");
                    progressDialog.setCancelable(true);
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

                    sendData();
                } else {
                    Toast.makeText(this, "format dd/mm/yyyy or your age is not between 16 to 70", Toast.LENGTH_LONG).show();
                }
            }
            else {
                Toast.makeText(this, "empty", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void sendData()
    {
        try {
            String strToSend = strMessage;
            // create SocketTask object
            SocketTask send1 = new SocketTask(strToSend);
            // string received from server
            String strReceived = send1.execute().get();
            System.out.println(strReceived);
            if (strReceived.equals("true") && strReceived != null)
                // start Verification activity
                startActivity(intent2);
            else
                Toast.makeText(this, "Sign up failed", Toast.LENGTH_LONG).show();
        }
        catch (ExecutionException | InterruptedException e)
        {
            Log.e("Exception", e.toString());
        }
    }

}