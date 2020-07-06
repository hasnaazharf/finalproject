package com.example.hasnasmarthome;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class MenuControl extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    Switch swAutoMode, swManualMode;
    Switch lampAuto, lampManual;

    NavigationView navigationView;
    DrawerLayout drawerlayout;
    ImageView menu_nav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_menu_control);

        //Menu Hooks
        drawerlayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        menu_nav = findViewById(R.id.icon_nav);

        navigationDrawer();

        //Auto Mode
        swAutoMode = (Switch) findViewById(R.id.switch_AutoMode);
        swAutoMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myref = database.getReference("Mode/Auto_Mode");
                    myref.setValue("1");
                    swManualMode.setChecked(false);
                    lampManual.setChecked(false);
                } else {
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myref = database.getReference("Mode/Auto_Mode");
                    myref.setValue("0");
                }
            }
        });

        //Manual Mode
        swManualMode = (Switch) findViewById(R.id.switch_ManualMode);
        lampManual = (Switch) findViewById(R.id.switch_ManualLamp);
        swManualMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myref = database.getReference("Mode/Manual_Mode");
                    myref.setValue("1");
                    swAutoMode.setChecked(false);
                } else {
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myref = database.getReference("Mode/Manual_Mode");
                    myref.setValue("0");
                }
            }
        });

        lampManual.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myref = database.getReference("Manual_Lamp_Status");
                    myref.setValue("ON");
                } else {
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myref = database.getReference("Manual_Lamp_Status");
                    myref.setValue("OFF");
                }
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

            case R.id.nav_graph:
                openMenuGraph();
                break;

            case R.id.nav_control:
                break;

            case R.id.nav_energy:
                openMenuEnergy();
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

    public void openHome (){
        Intent intent= new Intent(this, Dashboard.class);
        startActivity(intent);
    }
    public void openMenuStatus (){
        Intent intent= new Intent(this, MenuStatus.class);
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
        Intent intent= new Intent(this, MenuEnergy.class);
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