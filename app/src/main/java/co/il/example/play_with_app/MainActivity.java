package co.il.example.play_with_app;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    EditText etMail, etPassword;
    Button btnLogin, btnSignUp;
    Intent intent;
    String serverStr = "", strMessage = "";
    String email1, password1;
    String email, pass, username, game, gender, phone, level, city, date;

    ProgressDialog progressDialog;

    SharedPreferences sp;

    //    weather api
    final String APP_ID = "f97753fcd67bdba177f8aa329b9c8801";
    //    weather api
    final String WEATHER_URL = "https://api.openweathermap.org/data/2.5/weather";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get access to the shared preference file
        sp = getSharedPreferences("savedData", 0);

        // reference edit texts and buttons
        etMail = (EditText) findViewById(R.id.etMail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);
        // get email and password from shared preference file
        email1 = sp.getString("Email", null);
        password1 = sp.getString("Pass", null);
        // set the edit text text to the email and password from the shared preference file
        etMail.setText(email1);
        etPassword.setText(password1);
        // send to onClick function
        btnSignUp.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == btnLogin) {
            if (!etMail.getText().toString().equals("") &&
                    !etPassword.getText().toString().equals("")) {
                if (check_email(etMail.getText().toString()) &&
                        check_password(etPassword.getText().toString())) {
                    // create intent to pass after click to Tabs_Activity
                    intent = new Intent(this, Tabs_Activity.class);
                    // create message to server
                    strMessage = etMail.getText().toString() + "," + etPassword.getText().toString() + "1";
                    // create progress dialog while the client connect to server
                    progressDialog = ProgressDialog.show(this, "Please wait", "");
                    progressDialog.setCancelable(true);
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    // call send to server function
                    sendData();
                }
                else {
                    Toast.makeText(this, "try again", Toast.LENGTH_LONG).show();
                }
            }
            else {
                Toast.makeText(this, "empty", Toast.LENGTH_LONG).show();
            }
        }
        if (v == btnSignUp) {
            // pass to Sign up
            intent = new Intent(this, SignUp_Activity1.class);
            startActivity(intent);
        }
    }

    private void sendData() {
        try {
            String strToSend = strMessage;
            // create SocketTask object
            System.out.println(strToSend);
            SocketTask send1 = new SocketTask(strToSend);
            // string received from server
            String strReceived = send1.execute().get();
            System.out.println(strReceived);
            if (!strReceived.equals("false")) {
                // pass to Tabs_Activity
                // write to savedData file the details of the user
                String[] arr = strReceived.split(",");
                email = arr[0];
                pass = arr[1];
                phone = arr[2];
                username = arr[3];
                gender = arr[4];
                game = arr[5];
                city = arr[6];
                level = arr[7];
                date = arr[8];
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("Email", email);
                editor.putString("Pass", pass);
                editor.putString("Phone", phone);
                editor.putString("Username", username);
                editor.putString("Gender", gender);
                editor.putString("Game", game);
                editor.putString("City", city);
                editor.putString("Level", level);
                editor.putString("Date", date);
                editor.apply();
                startActivity(intent);
            }
            else
                Toast.makeText(this, "Login failed", Toast.LENGTH_LONG).show();
        } catch (ExecutionException | InterruptedException e) {
            Log.e("Exception", e.toString());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void addNotification(String title, String text) {
        if (Build.VERSION.SDK_INT >= 27){
            // create notification channel
            NotificationChannel channel = new NotificationChannel("Maor", "maor", NotificationManager.IMPORTANCE_DEFAULT);
            channel.enableVibration(true);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        // build the notification
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, "Maor")
                        .setSmallIcon(R.drawable.logo2)
                        .setContentTitle(title)
                        .setContentText(text)
                        .setColor(Color.rgb(255, 165, 0))
                        .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });
        // set sound for the notification
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
        r.play();


        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }


    // first method that runs when the app start
    @Override
    protected void onResume() {
        super.onResume();
        String city;
        if (sp.getString("City", "").equals("")){
            city = "Rishon LeZiyyon";
            System.out.println(city);
        }
        else {
            city = sp.getString("City", "");
        }
        if(city!=null)
        {
            System.out.println(city);
            getWeatherForCity(city);
        }
    }

    //    searching for the city
    private void getWeatherForCity(String city)
    {
        RequestParams params = new RequestParams();
        params.put("q",city);
        params.put("appid",APP_ID);
        request(params);
    }

    // request the json from the weather api
    private void request(RequestParams params)
    {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(WEATHER_URL,params,new JsonHttpResponseHandler()
        {

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response)
            {

                WeatherData weatherD = null;
                try {
                    weatherD = WeatherData.fromJson(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                notificationAlgorithm(weatherD);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            }
        });
    }

    // set the notification parameters according the weather
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void notificationAlgorithm(WeatherData weather)
    {
        System.out.println(weather.getmTemperature().substring(0, 2));
        if (Integer.parseInt(weather.getmTemperature().substring(0, 2)) < 20 &&
                Integer.parseInt(weather.getmTemperature().substring(0, 2)) > 10){
            addNotification("It is cold today", "Wear warm clothes");
        }
        if (Integer.parseInt(weather.getmTemperature().substring(0, 2)) >= 28){
            addNotification("It is hot today", "Take water with you");
        }
        if (weather.getmStat().equals("thunderstrom1") || weather.getmStat().equals("thunderstrom2") ||
                weather.getmStat().equals("shower") || weather.getmStat().equals("snow1") ||
                weather.getmStat().equals("snow2") ||
                Integer.parseInt(weather.getmTemperature().substring(0, 2)) < 10 ||
                Integer.parseInt(weather.getmTemperature().substring(0, 2)) > 35){
            addNotification("Not good weather", "You should not go to play today");
        }
        if (weather.getmStat().equals("sunny") ||
                Integer.parseInt(weather.getmTemperature().substring(0, 2)) < 30 &&
                Integer.parseInt(weather.getmTemperature().substring(0, 2)) > 20){
            addNotification("Good weather", "Nice day to play with friends");
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
        char[] signs = {'@', '#', '!', '$', '%', '^', '&', '*'};
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

}