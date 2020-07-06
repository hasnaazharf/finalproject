package com.example.hasnasmarthome;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Profile extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    TextInputLayout name, email, phoneNo, password, username;

    NavigationView navigationView;
    DrawerLayout drawerlayout;
    ImageView menu_nav;

    FirebaseDatabase rootNode;
    DatabaseReference reference;

    //Global Variable
    String user_name, user_username, user_email, user_phoneNo, user_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_profile);

        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference("users");

        //Hooks
        name = findViewById(R.id.full_name_profile);
        username = findViewById(R.id.username_profile);
        email = findViewById(R.id.email_profile);
        phoneNo = findViewById(R.id.phone_profile);
        password = findViewById(R.id.password_profile);

        //Menu Hooks
        drawerlayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        menu_nav = findViewById(R.id.icon_nav);

        navigationDrawer();
        showAllUserData();
    }

    private void showAllUserData() {

        Intent intent = getIntent();
        user_name = intent.getStringExtra("name");
        user_username = intent.getStringExtra("username");
        user_email = intent.getStringExtra("email");
        user_phoneNo = intent.getStringExtra("phoneNo");
        user_password = intent.getStringExtra("password");

        name.getEditText().setText(user_name);
        username.getEditText().setText(user_username);
        email.getEditText().setText(user_email);
        phoneNo.getEditText().setText(user_phoneNo);
        password.getEditText().setText(user_password);
    }

    public void update (View view){
        if(isNameChanged() || isPasswordChanged()){
            Toast.makeText(this, "Data has been Updated", Toast.LENGTH_LONG).show();
        }
    }

    private boolean isPasswordChanged() {
        if(!user_password.equals(password.getEditText().getText().toString()))
        {
            reference.child(user_username).child("password").setValue(password.getEditText().getText().toString());
            user_password = password.getEditText().getText().toString();
            return true;
        }else{
            return false;
        }
    }

    private boolean isNameChanged() {
        if(!user_username.equals(name.getEditText().getText().toString())){
            reference.child(user_username).child("name").setValue(name.getEditText().getText().toString());
            user_username = name.getEditText().getText().toString();
            return true;
        }else{
            return false;
        }
    }

    //Navigation Item

    private void navigationDrawer() {
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);

        menu_nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(drawerlayout.isDrawerVisible(GravityCompat.START))
                    drawerlayout.closeDrawer(GravityCompat.START);
                else drawerlayout.openDrawer(GravityCompat.START);
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_home:
                break;

            case R.id.nav_graph:
                openMenuGraph();
                break;

            case R.id.nav_control:
                openMenuControl();
                break;

            case R.id.nav_profile:
                openProfile();
                break;

            case R.id.nav_logout:
                openLogin();
                break;
        }

        drawerlayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed(){
        if(drawerlayout.isDrawerVisible(GravityCompat.START)){
            drawerlayout.closeDrawer(GravityCompat.START);
        }else
            super.onBackPressed();
    }

    public void openMenuStatus (){
        Intent intent= new Intent(this, MenuGraph.class);
        startActivity(intent);
    }
    public void openMenuGraph (){
        Intent intent= new Intent(this, MenuGraph.class);
        startActivity(intent);
    }
    public void openMenuControl (){
        Intent intent= new Intent(this, MenuControl.class);
        startActivity(intent);
    }
    public void openMenuEnergy (){
        Intent intent= new Intent(this, MenuGraph.class);
        startActivity(intent);
    }
    public void openProfile (){
        Intent intent= new Intent(this, Profile.class);
        startActivity(intent);
    }
    public void openLogin () {
        Intent intent= new Intent(this, Login.class);
        startActivity(intent);
    }
}
