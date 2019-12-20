package com.firefly.sunrise;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class frmView extends AppCompatActivity {

    ListView myorders;
    private Spinner comboitemname;
    private ImageView imgdate;
    ValueEventListener listner;
    ArrayAdapter<String> adapter;
    ArrayList<String> spinnerdatalist;
;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frm_view);


       myorders=(ListView) findViewById(R.id.listorders);

        Query get = FirebaseDatabase.getInstance().getReference("Orders");


        spinnerdatalist = new ArrayList<>();
        adapter=new ArrayAdapter<String>(frmView.this,android.R.layout.simple_spinner_dropdown_item,spinnerdatalist);

        myorders.setAdapter(adapter);

        listner=get.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot item:dataSnapshot.getChildren()){
                    //  spinnerdatalist.add(item.getValue().toString());

                    // spinnerdatalist.add(item.child("cusname").getValue().toString());
                    spinnerdatalist.add(item.child("itemname").getValue().toString());
                    spinnerdatalist.add(item.child("qty").getValue().toString());
                    spinnerdatalist.add(item.child("orderdate").getValue().toString());






                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
