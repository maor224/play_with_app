package co.il.example.play_with_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SignUp_Activity2 extends AppCompatActivity implements View.OnClickListener {

    EditText etUsername;
    Button btnBoy, btnGirl, btnBasketball, btnSoccer, btnTennis, btnPingPong, btnVolleyball;
    Button btnContinue2;
    String email, pass, phone;
    int i = 1;
    Intent intent2;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up2);

        etUsername = (EditText)findViewById(R.id.etUsername);
        btnBoy = (Button)findViewById(R.id.btnBoy);
        btnGirl = (Button)findViewById(R.id.btnGirl);
        btnBasketball = (Button)findViewById(R.id.btnBasketball);
        btnSoccer = (Button)findViewById(R.id.btnSoccer);
        btnTennis = (Button)findViewById(R.id.btnTennis);
        btnPingPong = (Button)findViewById(R.id.btnPingPong);
        btnVolleyball = (Button)findViewById(R.id.btnVolleyball);
        btnContinue2 = (Button)findViewById(R.id.btnContinue2);

        btnBoy.setOnClickListener(this);
        btnGirl.setOnClickListener(this);
        btnBasketball.setOnClickListener(this);
        btnSoccer.setOnClickListener(this);
        btnTennis.setOnClickListener(this);
        btnPingPong.setOnClickListener(this);
        btnVolleyball.setOnClickListener(this);
        btnContinue2.setOnClickListener(this);
        // get email, password and phone from previous screen
        Intent intent = getIntent();
        email = intent.getExtras().getString("Email");
        pass = intent.getExtras().getString("Pass");
        phone = intent.getExtras().getString("Phone");

    }


    @Override
    public void onClick(View v) {
        if (i == 1) {
            intent2 = new Intent(this, SignUp_Activity3.class);
            i++;
        }
        if (v == btnBoy){
            Toast.makeText(this, "male", Toast.LENGTH_LONG).show();
            intent2.putExtra("Gender", "male");
        }
        if (v == btnGirl){
            Toast.makeText(this, "female", Toast.LENGTH_LONG).show();
            intent2.putExtra("Gender", "female");
        }
        if (v == btnBasketball){
            Toast.makeText(this, "basketball", Toast.LENGTH_LONG).show();
            intent2.putExtra("Game", "basketball");
        }
        if (v == btnSoccer){
            Toast.makeText(this, "soccer", Toast.LENGTH_LONG).show();
            intent2.putExtra("Game", "soccer");
        }
        if (v == btnTennis){
            Toast.makeText(this, "tennis", Toast.LENGTH_LONG).show();
            intent2.putExtra("Game", "tennis");
        }
        if (v == btnPingPong){
            Toast.makeText(this, "ping pong", Toast.LENGTH_LONG).show();
            intent2.putExtra("Game", "ping_pong");
        }
        if (v == btnVolleyball){
            Toast.makeText(this, "volleyball", Toast.LENGTH_LONG).show();
            intent2.putExtra("Game", "volleyball");
        }
        if (v == btnContinue2){
            // pass the email, password, phone, username, gender and game to the next screen
            String username = etUsername.getText().toString();
            if (!username.equals("") && username.length() > 3) {
                intent2.putExtra("Username", username);
                intent2.putExtra("Email", email);
                intent2.putExtra("Pass", pass);
                intent2.putExtra("Phone", phone);
                startActivity(intent2);
            }
            else {
                Toast.makeText(this, "Username must be at least 3 characters", Toast.LENGTH_LONG).show();
            }
        }
     }
}