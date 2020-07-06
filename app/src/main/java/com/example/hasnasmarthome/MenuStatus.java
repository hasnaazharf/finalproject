package com.example.hasnasmarthome;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MenuStatus extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    //Drawer Menu
    NavigationView navigationView;
    DrawerLayout drawerlayout;
    ImageView menu_nav;

    private TextView textViewResult;
    private RequestQueue mQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_menu_status);

        //Menu Hooks
        drawerlayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        menu_nav = findViewById(R.id.icon_nav);
        textViewResult = findViewById(R.id.text_view_status);
        Button buttonGet = findViewById(R.id.button_get);

        mQueue = Volley.newRequestQueue(this);
        buttonGet.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View v){
                jsonParse();
            }
        });

        navigationDrawer();
    }

    private void jsonParse(){
        String url = "https://us-central1-energy-monitoring-6c8ab.cloudfunctions.net/app/api";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            JSONArray jsonArray = response.getJSONArray("energy_daily");

                            for (int i = 0; i < jsonArray.length(); i++){
                                JSONObject energy_daily = jsonArray.getJSONObject(i);
                            }
                        } catch (JSONException e){
                            e.printStackTrace();
                        }

                    }
                },

                new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
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
                break;

            case R.id.nav_graph:
                openMenuGraph();
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
