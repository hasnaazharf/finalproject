package com.example.hasnasmarthome;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class Profile extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    TextInputLayout name, email, phoneNo, password, username;
    Button update_btn;

    NavigationView navigationView;
    DrawerLayout drawerlayout;
    ImageView menu_nav;

    // Get a reference to our posts
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference("users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_profile);

        //Hooks
        name = findViewById(R.id.full_name_profile);
        username = findViewById(R.id.username_profile);
        username.setEnabled(false);
        email = findViewById(R.id.email_profile);
        phoneNo = findViewById(R.id.phone_profile);
        password = findViewById(R.id.password_profile);

        //Menu Hooks
        drawerlayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        menu_nav = findViewById(R.id.icon_nav);

        update_btn = (Button) findViewById(R.id.update_btn);
        update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editData();
            }
        });
        navigationDrawer();
        showAllUserData();
    }

    private void editData() {
        String enteredFullname = name.getEditText().getText().toString();
        String enteredUsername = username.getEditText().getText().toString();
        String enteredEmail = email.getEditText().getText().toString();
        String enteredPhoneNo = phoneNo.getEditText().getText().toString();
        String enteredPassword = password.getEditText().getText().toString();

        UserHelperClass helperClass = new UserHelperClass(enteredFullname,enteredUsername,enteredEmail,enteredPhoneNo,enteredPassword);
        Intent intent = getIntent();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(intent.getStringExtra("username"));
        ref.setValue(helperClass);
        Toast.makeText(getApplicationContext(), "Successfully updated user", Toast.LENGTH_SHORT).show();
    }

    private void showAllUserData() {

        Intent intent = getIntent();
        username.getEditText().setText(intent.getStringExtra("username"));

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(intent.getStringExtra("username"));
        ref.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot){
                email.getEditText().setText(dataSnapshot.child("email").getValue().toString());
                name.getEditText().setText(dataSnapshot.child("name").getValue().toString());
                phoneNo.getEditText().setText(dataSnapshot.child("phoneNo").getValue().toString());
                password.getEditText().setText(dataSnapshot.child("password").getValue().toString());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError){
                //
            }
        });
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
                openHome();
                break;

            case R.id.nav_status:
                openMenuStatus();
                break;

            case R.id.nav_chart:
                openMenuChart();
                break;

            case R.id.nav_control:
                openMenuControl();
                break;

            case R.id.nav_energy:
                openMenuEnergy();
                break;

            case R.id.nav_profile:
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

    public void openHome (){
        Intent intent= new Intent(this, Dashboard.class);
        startActivity(intent);
    }

    public void openMenuStatus (){
        Intent intent= new Intent(this, MenuStatus.class);
        startActivity(intent);
    }
    public void openMenuChart (){
        Intent intent= new Intent(this, MenuChart.class);
        startActivity(intent);
    }
    public void openMenuControl (){
        Intent intent= new Intent(this, MenuControl.class);
        startActivity(intent);
    }
    public void openMenuEnergy (){
        Intent intent= new Intent(this, MenuEnergy.class);
        startActivity(intent);
    }
    public void openLogin () {
        Intent intent= new Intent(this, Login.class);
        startActivity(intent);
    }
}