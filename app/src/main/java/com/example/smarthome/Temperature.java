package com.example.smarthome;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Temperature extends Fragment {

    FirebaseDatabase database;
    DatabaseReference reference, green, red;
    SwitchCompat ac,heater,manual;

    ProgressBar fah, cel, hum;

    double f1=88, c1;
    String temp,h;
    TextView fahrenheit,humidity,celcius;

    Handler handler = new Handler();
    Runnable runnableCode;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_temperature, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fahrenheit = view.findViewById(R.id.fahrenheit);
        celcius = view.findViewById(R.id.celcius);
        humidity = view.findViewById(R.id.humidity);
        ac = view.findViewById(R.id.ac);
        heater = view.findViewById(R.id.heater);
        manual = view.findViewById(R.id.manualOverride);

        fah = view.findViewById(R.id.progressBarFah);
        cel = view.findViewById(R.id.progressBarCelcius);
        hum = view.findViewById(R.id.progressBarHumidity);

        fahrenheit.setText("");
        celcius.setText("");
        humidity.setText("");

        setManual(0);
        manual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(manual.isChecked()) {
                    ac.setEnabled(true);
                    heater.setEnabled(true);
                    setManual(1);
                }
                else {
                    ac.setEnabled(false);
                    heater.setEnabled(false);
                    green.child("greenLed").setValue("0");
                    red.child("redLed").setValue("0");
                    setManual(0);
                }
            }
        });


        reference = FirebaseDatabase.getInstance().getReference().child("RoomTemp");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                h = snapshot.child("humidity").getValue().toString();
                temp = snapshot.child("temperature").getValue().toString();
                c1 = Float.parseFloat(temp);
                f1 = (9 * c1 / 5) + 32;

                fahrenheit.setText(f1 + "\nFahrenheit");
                fah.setProgress((int)f1);
                celcius.setText(temp + "\nCelcius");
                cel.setProgress((int)c1);
                humidity.setText(h + "\nHumidity");
                hum.setProgress(Integer.valueOf(h));

                if(manual.isChecked()) {
                    setManual(1);
                }
                else
                    setManual(0);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        green = FirebaseDatabase.getInstance().getReference().child("RoomTemp");
        red = FirebaseDatabase.getInstance().getReference().child("RoomTemp");
        ac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Check1", String.valueOf(ac.isChecked()));
                if(ac.isChecked()) {
                    green.child("greenLed").setValue("1");
                    red.child("redLed").setValue("0");
                    heater.setEnabled(false);
                }
                else {
                    heater.setEnabled(true);
                    green.child("greenLed").setValue("0");
                    red.child("redLed").setValue("0");
                }
            }
        });

        heater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Check2", String.valueOf(ac.isChecked()));
                if(heater.isChecked()){
                    ac.setEnabled(false);
                    red.child("redLed").setValue("1");
                    green.child("greenLed").setValue("0");
                }
                else{
                    ac.setEnabled(true);
                    green.child("greenLed").setValue("0");
                    red.child("redLed").setValue("0");
                }
            }
        });


        runnableCode = new Runnable() {
            @Override
            public void run() {
                Log.i("Check", String.valueOf(f1));
                if (f1 >= 90) {
                    Log.d("ac status", "ac on");
                    ac.setChecked(true);
                    heater.setChecked(false);

                    green.child("greenLed").setValue("1");
                    red.child("redLed").setValue("0");
                } else if (f1 <= 80) {
                    ac.setChecked(false);
                    heater.setChecked(true);

                    red.child("redLed").setValue("1");
                    green.child("greenLed").setValue("0");
                } else {
                    Log.d("ac status", "ac off");
                    heater.setChecked(false);
                    ac.setChecked(false);

                    green.child("greenLed").setValue("0");
                    red.child("redLed").setValue("0");
                }
                handler.postDelayed(this, 2000);
            }
        };
    }

    public void setManual(int m){
        if(m==0){
            handler.post(runnableCode);
        }
        else {
            handler.removeCallbacks(runnableCode);
        }
    }

}