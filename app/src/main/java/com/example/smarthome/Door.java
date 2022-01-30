package com.example.smarthome;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Door extends Fragment {

    private ImageView doorClose,doorOpen;
    FirebaseDatabase database;
    DatabaseReference reference;
    private final int id=0;
    private int flag=0;
    private EditText lostRfid;
    private String status = "Door Status";
    private Button unlockDoor;
    private TextView textView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_door, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        doorClose = view.findViewById(R.id.doorClose);
        doorOpen = view.findViewById(R.id.doorOpen);
        unlockDoor = view.findViewById(R.id.unlockButton);
        lostRfid = view.findViewById(R.id.lostRfid);
        textView = view.findViewById(R.id.textView);

        textView.setText("");
        reference = FirebaseDatabase.getInstance().getReference().child("RoomDoor");

        unlockDoor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = flag + 1;
                String pin = lostRfid.getText().toString();

                String corrPin = "1234";
                if(pin.equals(corrPin))
                {
                    lostRfid.setText("");
                    doorClose.setVisibility(View.GONE);
                    doorOpen.setVisibility(View.VISIBLE);
                    reference.child("status").setValue("OPEN");
                    unlockDoor.setEnabled(false);
                }
                else{
                    if(flag==3)
                    {
                        lostRfid.setText("");
                        reference.child("status").setValue("CLOSED");
                        lostRfid.setEnabled(false);
                        textView.setText("Max tries reached ! Try again after sometime");
                        unlockDoor.setEnabled(false);
                    }
                    else
                    {
                        lostRfid.setText("");
                        textView.setText("Wrong pin ! Try again");
                    }
                }

            }
        });

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot :snapshot.getChildren()) {
                    status = dataSnapshot.getValue().toString();
                }
                textView.setText(status);

                if(status.equals("CLOSED")){
                    doorOpen.setVisibility(View.GONE);
                    doorClose.setVisibility(View.VISIBLE);
                    Log.d("Function","if() entered");
                    if(flag<3)
                        unlockDoor.setEnabled(true);
                    //notification();
                }
                else
                {
                    doorClose.setVisibility(View.GONE);
                    doorOpen.setVisibility(View.VISIBLE);
                    unlockDoor.setEnabled(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    /*
    private void notification(){

        Log.d("Status2","Notif() entered");

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("n","n", NotificationManager.IMPORTANCE_HIGH);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);

        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,"n").setContentTitle("Alert from NodeMCU").setSmallIcon(R.drawable.ic_warning)
                .setAutoCancel(true).setContentText("Intruder Detected !");

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(0,builder.build());
    }

     */
}