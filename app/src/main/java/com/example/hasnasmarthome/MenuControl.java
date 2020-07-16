package com.example.hasnasmarthome;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mcsoft.timerangepickerdialog.RangeTimePickerDialog;


public class MenuControl extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, RangeTimePickerDialog.ISelectedTime{

    Switch swAutoMode, swManualMode;
    Switch lampManual;

    NavigationView navigationView;
    DrawerLayout drawerlayout;
    ImageView menu_nav;

    Button button;
    TextView timeON, timeOFF;

    final class selectedTime
    {
        public int hourStart, minuteStart, hourEnd, minuteEnd;

        public selectedTime(int hourStart, int minuteStart, int hourEnd, int minuteEnd) {
            this.hourStart = hourStart;
            this.minuteStart = minuteStart;
            this.hourEnd = hourEnd;
            this.minuteEnd = minuteEnd;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_menu_control);

        //Menu Hooks
        drawerlayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        menu_nav = findViewById(R.id.icon_nav);
        timeON = (TextView) findViewById(R.id.textViewON);
        timeOFF = (TextView) findViewById(R.id.textViewOFF);
        button = (Button) findViewById(R.id.buttonLamp1);

        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                showCustomDialogTimePicker();
            }
        });

        navigationDrawer();

        //Auto Mode
        swAutoMode = (Switch) findViewById(R.id.switch_AutoMode);
        swAutoMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference auto = database.getReference("Mode/Auto_Mode");
                    auto.setValue("ON");
                    DatabaseReference manual = database.getReference("Mode/Manual_Mode");
                    manual.setValue("OFF");

                    swManualMode.setChecked(false);
                    lampManual.setChecked(false);
                    timeON.setEnabled(true);
                    timeOFF.setEnabled(true);
                    button.setEnabled(true);
                } else {
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference auto = database.getReference("Mode/Auto_Mode");
                    auto.setValue("OFF");
                    DatabaseReference manual = database.getReference("Mode/Manual_Mode");
                    manual.setValue("ON");
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
                    DatabaseReference manual = database.getReference("Mode/Manual_Mode");
                    manual.setValue("ON");
                    DatabaseReference auto = database.getReference("Mode/Auto_Mode");
                    auto.setValue("OFF");
                    swAutoMode.setChecked(false);
                    timeON.setEnabled(false);
                    timeOFF.setEnabled(false);
                    button.setEnabled(false);
                } else {
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference manual = database.getReference("Mode/Manual_Mode");
                    manual.setValue("OFF");
                    DatabaseReference auto = database.getReference("Mode/Auto_Mode");
                    auto.setValue("ON");
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

    public void showCustomDialogTimePicker()
    {
        // Create an instance of the dialog fragment and show it
        RangeTimePickerDialog dialog = new RangeTimePickerDialog();
        dialog.newInstance();
        dialog.setIs24HourView(true);
        dialog.setRadiusDialog(20);
        dialog.setTextTabStart("Start");
        dialog.setTextTabEnd("End");
        dialog.setTextBtnPositive("Accept");
        dialog.setTextBtnNegative("Close");
        dialog.setValidateRange(false);
        dialog.setColorBackgroundHeader(R.color.pink);
        dialog.setColorBackgroundTimePickerHeader(R.color.pink);
        dialog.setColorTextButton(R.color.white);
        FragmentManager fragmentManager = getFragmentManager();
        dialog.show(fragmentManager, "");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSelectedTime(int hourStart, int minuteStart, int hourEnd, int minuteEnd)
    {
        selectedTime selectTime = new selectedTime(hourStart, minuteStart, hourEnd, minuteEnd);
        Intent intent = getIntent();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Auto_Time");
        ref.setValue(selectTime);

        timeON.setText(new StringBuilder().append(selectTime.hourStart).append(" : ").append(selectTime.minuteStart)
                .append(" "));
        timeOFF.setText(new StringBuilder().append(selectTime.hourEnd).append(" : ").append(selectTime.minuteEnd)
                .append(" "));


        Toast.makeText(this, "Start Time : "+hourStart+":"+minuteStart+"\nEnd Time: "+hourEnd+":"+minuteEnd, Toast.LENGTH_SHORT).show();
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
    public void openProfile (){
        Intent intent= new Intent(this, Profile.class);
        startActivity(intent);
    }
    public void openLogin () {
        Intent intent= new Intent(this, Login.class);
        startActivity(intent);
    }
}