package com.example.hasnasmarthome;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

import static com.example.hasnasmarthome.App.CHANNEL_1_ID;

public class Consumption extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private NotificationManagerCompat notificationManager;

    private static DecimalFormat df = new DecimalFormat("0.000");
    private static DecimalFormat dec = new DecimalFormat("0");

    private TextView compareView, hoursView, persentageView, consumptionView;
    private RequestQueue mQueue;
    String secondStr, consumptionStr;

    //Drawer Menu
    NavigationView navigationView;
    DrawerLayout drawerlayout;
    ImageView menu_nav;

    FirebaseDatabase rootNode;
    DatabaseReference reference;

    String username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consumption);

        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference("users");

        //Menu Hooks
        drawerlayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        menu_nav = findViewById(R.id.icon_nav);

        navigationDrawer();

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        password = intent.getStringExtra("password");

        notificationManager = NotificationManagerCompat.from(this);

        compareView = findViewById(R.id.compare);
        hoursView = findViewById(R.id.hoursDouble);
        persentageView = findViewById(R.id.persentage);
        consumptionView = findViewById(R.id.consumptionkwh);

        mQueue = Volley.newRequestQueue(this);

        jsonParse();
        showData();
        compareData();
        persentage();
    }

    private void persentage() {

        final EnergyHelperClass helperClass = com.example.hasnasmarthome.EnergyHelperClass.getInstance();
        final double seconds  = helperClass.getSeconds();
        final double consumption  = helperClass.getConsumption();

        double percent = (double) seconds/consumption*100;
        persentageView.setText(new StringBuilder().append(df.format(percent)).append(" % "));

    }
    private void jsonParse() {
        AndroidNetworking.get("https://us-central1-energy-monitoring-6c8ab.cloudfunctions.net/app/api/last_day_energy")
                .setTag("test")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // do anything with response
                            Log.d("_DATA", response.toString());

                            double secondDouble = response.getDouble("seconds");
                            final EnergyHelperClass helperClass = com.example.hasnasmarthome.EnergyHelperClass.getInstance();
                            helperClass.setSeconds(secondDouble);

                            double hourDouble = secondDouble/3600;
                            hoursView.setText(new StringBuilder().append(dec.format(hourDouble)).append(" Hours Today "));


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error
                    }
                });
    }
    private void showData() {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Energy/Seconds");

        //Adding a listener for the value associated with the user key "0"
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                //In this case, "shalom" will be stored in mName
                consumptionStr = (String) snapshot.getValue();
                double consumptionDouble = Double.parseDouble(consumptionStr); // returns double primitive

                final EnergyHelperClass helperClass = com.example.hasnasmarthome.EnergyHelperClass.getInstance();
                helperClass.setConsumption(consumptionDouble);

                double consumptionkwh = consumptionDouble/3600*5/1000;
                consumptionView.setText(new StringBuilder().append("Your Limit is ").append(df.format(consumptionkwh)).append(" kWh per Day"));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    private void compareData(){
        final EnergyHelperClass helperClass = com.example.hasnasmarthome.EnergyHelperClass.getInstance();
        final double seconds = helperClass.getSeconds();
        final double consumption  = helperClass.getConsumption();

        int compare = Double.compare(consumption, seconds);

        if(compare > 0) {
            compareView.setText(new StringBuilder().append("Your Energy Consumption still under Limit"));
        } else if(compare < 0) {
            compareView.setText(new StringBuilder().append("Your Energy Consumption Exceeds Limit"));
            sendNotification();
        } else {
            compareView.setText(new StringBuilder().append("Your Energy Consumption Exceeds Limit"));
            sendNotification();
        }
    }
    public void sendNotification() {
        String message = "Click to See Tips to Reduce Energy Consumption";

        Intent activityIntent = new Intent(this, Consumption.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                0, activityIntent, 0);

        Intent broadcastIntent = new Intent(this, Tips.class);
        broadcastIntent.putExtra("toastMessage", message);
        PendingIntent actionIntent = PendingIntent.getBroadcast(this,
                0, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        android.app.Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.icon_smarthome)
                .setContentTitle("YOUR ENERGY CONSUMPTION EXCEEDS LIMIT")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setColor(getResources().getColor(R.color.pink))
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .addAction(R.mipmap.ic_launcher, "Click", actionIntent)
                .build();

        notificationManager.notify(1, notification);
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
        Intent intent = new Intent(this, Profile.class);
        intent.putExtra("username",username);
        intent.putExtra("password",password);
        startActivity(intent);
    }
    public void openLogin () {
        Intent intent= new Intent(this, Login.class);
        startActivity(intent);
    }


}