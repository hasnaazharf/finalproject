package com.example.hasnasmarthome;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MenuEnergy extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //Drawer Menu
    NavigationView navigationView;
    DrawerLayout drawerlayout;
    ImageView menu_nav;

    FirebaseDatabase rootNode;
    DatabaseReference reference;

    String username, password, second;

    Button button_low, button_mid, button_high;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_menu_energy);

        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference("users");

        //Menu Hooks
        drawerlayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        menu_nav = findViewById(R.id.icon_nav);

        button_low = findViewById(R.id.low);
        button_mid = findViewById(R.id.moderate);
        button_high = findViewById(R.id.high);

        navigationDrawer();
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        password = intent.getStringExtra("password");

        button_low.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lowConsumption();
            }
        });

        button_mid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moderateConsumption();
            }
        });

        button_high.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                highConsumption();
            }
        });

    }

    public void lowConsumption(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference seconds = database.getReference("Energy/Seconds");
        seconds.setValue("28800");

        Intent intent = new Intent(this, Consumption.class);
        intent.putExtra("Energy/Seconds",second);
        startActivity(intent);

    }

    public void moderateConsumption(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference seconds = database.getReference("Energy/Seconds");
        seconds.setValue("50400");

        Intent intent = new Intent(this, Consumption.class);
        intent.putExtra("Energy/Seconds",second);
        startActivity(intent);
    }

    public void highConsumption(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference seconds = database.getReference("Energy/Seconds");
        seconds.setValue("72000");

        Intent intent = new Intent(this, Consumption.class);
        intent.putExtra("Energy/Seconds",second);
        startActivity(intent);
    }

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
    public void openProfile (){
        Intent intent = new Intent(this, Profile.class);
        intent.putExtra("username",username);
        intent.putExtra("password",password);
        startActivity(intent);
    }

}
