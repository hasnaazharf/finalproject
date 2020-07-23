package com.example.hasnasmarthome;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

    private static int SPLASH_SCREEN = 5000;

    Button callSignUp, login_btn;
    ImageView login;
    TextView logoText, sloganText;
    TextInputLayout username, password;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); //This Line will hide the status bar from the screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        //Hooks

        login = findViewById(R.id.login_image);
        logoText = findViewById(R.id.login_name);
        sloganText = findViewById(R.id.slogan_name);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);

        callSignUp = (Button) findViewById(R.id.signup_screen);
        callSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSignUpScreen();
            }
        });

        login_btn = (Button) findViewById(R.id.login_btn);

        sp = getSharedPreferences("login",MODE_PRIVATE);

        if(sp.getBoolean("logged",false)){
            goToDasboard();
        }

    }

    public void openSignUpScreen (){
        Intent intent= new Intent(this, SignUp.class);
        startActivity(intent);
    }

    private Boolean validateUsername() {
        String val = username.getEditText().getText().toString();
        if (val.isEmpty()) {
            username.setError("Field cannot be empty");
            return false;
        } else {
            username.setError(null);
            username.setErrorEnabled(false);
            return true;
        }
    }
    private Boolean validatePassword() {
        String val = password.getEditText().getText().toString();
        if (val.isEmpty()) {
            password.setError("Field cannot be empty");
            return false;
        } else {
            password.setError(null);
            password.setErrorEnabled(false);
            return true;
        }
    }

    public void loginUser(View view) {
        //Validate Login Info
        if (!validateUsername() | !validatePassword()) {
            return;
        } else {
            isUser();
        }
    }

    private void isUser() {
        final String userEnteredUsername = username.getEditText().getText().toString().trim();
        final String userEnteredPassword = password.getEditText().getText().toString().trim();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query checkUser = reference.orderByChild("username").equalTo(userEnteredUsername);
        checkUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    username.setError(null);
                    username.setErrorEnabled(false);

                    String passwordFromDB = dataSnapshot.child(userEnteredUsername).child("password").getValue(String.class);
                    if (passwordFromDB.equals(userEnteredPassword)) {
                        username.setError(null);
                        username.setErrorEnabled(false);
                        String usernameFromDB = dataSnapshot.child(userEnteredUsername).child("username").getValue(String.class);
                        sp.edit().putBoolean("logged",true).apply();
                        sp.edit().putString("username",usernameFromDB).apply();
                        sp.edit().putString("password",passwordFromDB).apply();
                        goToDasboard();
                    } else {
                        password.setError("Wrong Password");
                        password.requestFocus();
                    }
                } else {
                    username.setError("No such User exist");
                    username.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void goToDasboard(){
        Intent intent = new Intent(this, Dashboard.class);
        intent.putExtra("username",sp.getString("username",""));
        intent.putExtra("password",sp.getString("password",""));
        Log.i("hehehe",sp.getString("username",""));

        startActivity(intent);
    }
}

