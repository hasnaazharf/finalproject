package com.example.hasnasmarthome;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class MenuEnergy extends Activity {
    private TimePicker timePickerStart, timePickerEnd;
    private TabItem tabItemStartTime, tabItemEndTime;
    private TabLayout tabLayout;
    private TextView timeON, timeOFF;
    private Calendar calendar;
    private String format = "";
    private Button buttonOK, buttonCancel;
    private boolean is24HourView = true;

    final class selectedONTime
    {
        public int hourStart, minStart;

        public selectedONTime(int hourStart, int minStart) {
            this.hourStart = hourStart;
            this.minStart = minStart;
        }
    }

    final class selectedOFFTime
    {
        public int hourEnd, minEnd;

        public selectedOFFTime(int hourEnd, int minEnd) {
            this.hourEnd = hourEnd;
            this.minEnd = minEnd;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_energy);

        this.is24HourView = true;

        timePickerStart = (TimePicker) findViewById(R.id.timePickerStart);
        timePickerEnd = (TimePicker) findViewById(R.id.timePickerEnd);
        timeON = (TextView) findViewById(R.id.textViewON);
        timeOFF = (TextView) findViewById(R.id.textViewOFF);

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        calendar = Calendar.getInstance();

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);

        buttonOK = (Button) findViewById(R.id.btnOK);
        buttonOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getStartTime();
                getEndTime();
            }
        });

        buttonCancel = (Button) findViewById(R.id.btnCancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    private void getStartTime() {
        int hourStart = timePickerStart.getCurrentHour();
        int minStart = timePickerStart.getCurrentMinute();

        selectedONTime selectONTime = new selectedONTime(hourStart, minStart);
        Intent intent = getIntent();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Auto_Time");
        ref.setValue(selectONTime);

        timeON.setText(new StringBuilder().append(selectONTime.hourStart).append(" : ").append(selectONTime.minStart)
                .append(" ").append(format));

        Toast.makeText(getApplicationContext(), "Successfully Updated Time", Toast.LENGTH_SHORT).show();
    }

    private void getEndTime() {
        int hourEnd = timePickerEnd.getCurrentHour();
        int minEnd = timePickerEnd.getCurrentMinute();

        selectedOFFTime selectTime = new selectedOFFTime(hourEnd, minEnd);
        Intent intent = getIntent();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Auto_Time");
        ref.setValue(selectTime);

        timeOFF.setText(new StringBuilder().append(selectTime.hourEnd).append(" : ").append(selectTime.minEnd)
                .append(" ").append(format));

        Toast.makeText(getApplicationContext(), "Successfully Updated Time", Toast.LENGTH_SHORT).show();
    }

}
