package com.firefly.sunrise;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class frmRate extends AppCompatActivity {

    private RatingBar rb;
    private EditText txtcomment;
    private Button btnsubmit;

    DatabaseReference reff;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    String esource;
    String keys;

    String crate;
    String ccomment;

    Float ratSize;

    Rating rating;
    long maxid=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frm_rate);

        firebaseAuth=FirebaseAuth.getInstance();

        rate();
        getEmail();
        //currentStatus();
        btnEvent();
        //verifyUser();
      //  getRateComment();



    }

    //startUp
    public void startupDb(){

        reff = FirebaseDatabase.getInstance().getReference().child("Customer_Rate");

        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    maxid = dataSnapshot.getChildrenCount();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //rate
    public void rate(){

        rb=(RatingBar) findViewById(R.id.txtrate);
        txtcomment=(EditText) findViewById(R.id.txtcomment);
        btnsubmit=(Button) findViewById(R.id.btnrate);

        rb.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                ratSize=rating;

            }
        });
    }

    //getEmail
    public void getEmail(){
        try {
            firebaseUser = firebaseAuth.getCurrentUser();
            esource = firebaseUser.getEmail();

        }
        catch (Exception e)
        {
            Toast.makeText(frmRate.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    //getCurrentStatus
    public void currentStatus(){
        Query get = FirebaseDatabase.getInstance().getReference("customer_details").orderByChild("cusemail").equalTo(esource);

        get.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot ds: dataSnapshot.getChildren()){
                     keys=ds.getKey();
                     crate=ds.child("cusrate").getValue().toString();
                     ccomment=ds.child("cuscomment").getValue().toString();

               }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //btnEvent
    public void btnEvent(){
        btnsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtcomment.getText().toString().equals("") || txtcomment.getText().toString().equals("stillnot") || ratSize<=0){
                    Toast.makeText(frmRate.this, "Please rate and provide a valid comment", Toast.LENGTH_SHORT).show();

                }
                else{
                    verifyUser();
                }
            }
        });
    }

    //verify user
    public void verifyUser(){
        try {

            DatabaseReference reff= FirebaseDatabase.getInstance().getReference().child("Customer_Rate");

            reff.orderByChild("email").equalTo(esource).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        alreadyRate();


                    } else {
                       newUser();

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }
        catch (Exception e){
            Toast.makeText(frmRate.this, e.getMessage(), Toast.LENGTH_SHORT).show();

        }
    }

    //new user
    public void newUser(){
    try{
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
        final String formattedDate = df.format(c);

        rating=new Rating();
        reff= FirebaseDatabase.getInstance().getReference().child("Customer_Rate");

        String email=esource.trim();
        String date=formattedDate.trim();
        Float r=ratSize;
        String cm=txtcomment.getText().toString();

        rating.setEmail(email);
        rating.setDate(date);
        rating.setRate(r);
        rating.setComment(cm);


        reff.child(String.valueOf(maxid + 1)).setValue(rating);

        txtcomment.setText("");
        rb.setRating(0F);

        logoutAlert();
    }
        catch (Exception e){
        Toast.makeText(frmRate.this, e.getMessage(), Toast.LENGTH_SHORT).show();

    }
    }

    //alreadyRate
    public void alreadyRate(){
        try{
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
        final String formattedDate = df.format(c);

        reff= FirebaseDatabase.getInstance().getReference();

        reff.child("Customer_Rate").child("1").child("date").setValue(formattedDate.trim());
        reff.child("Customer_Rate").child("1").child("rate").setValue(ratSize);
        reff.child("Customer_Rate").child("1").child("comment").setValue(txtcomment.getText().toString().trim());
        txtcomment.setText("");
        rb.setRating(0F);
            logoutAlert();
    }
        catch (Exception e){
        Toast.makeText(frmRate.this, e.getMessage(), Toast.LENGTH_SHORT).show();

    }
    }

    //sweet
    //swetalert
    public void logoutAlert(){
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Thank you")
                .setContentText("Thank you for rate us.Your review help us to buld stronger application ")
                .setConfirmText("Ok").setConfirmButtonBackgroundColor(R.color.colorPrimary).setConfirmButtonTextColor(R.color.colorPrimary)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {

                        finish();

                    }
                }).setCancelButtonBackgroundColor(R.color.colorPrimary).setCancelButtonTextColor(R.color.colorPrimary)
                .setCancelButton("Cancel", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                    }
                })
                .show();
    }


}
