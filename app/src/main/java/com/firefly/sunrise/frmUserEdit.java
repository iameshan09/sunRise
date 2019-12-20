package com.firefly.sunrise;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class frmUserEdit extends AppCompatActivity {

    private EditText name;
    private EditText nic;
    private EditText email;
    private EditText contact;
    private EditText address;
    private Button btnupdate;
    public String esource;

    String keys;

    DatabaseReference reff;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    SweetAlertDialog pDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frm_user_edit);

        firebaseAuth=FirebaseAuth.getInstance();


        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);
        pDialog.show();


        getEmail();
        getAccount();
        validate();

    }

    //getEmail
    public void getEmail(){
        try {
            firebaseUser = firebaseAuth.getCurrentUser();
            esource = firebaseUser.getEmail();
        }
        catch (Exception e)
        {
            Toast.makeText(frmUserEdit.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    //retriveAcount
    public void getAccount(){
        try{



               name=(EditText) findViewById(R.id.txtname);
               nic=(EditText) findViewById(R.id.txtnic);
               email=(EditText) findViewById(R.id.txtemail);
               contact=(EditText) findViewById(R.id.txtcontact);
               address=(EditText) findViewById(R.id.txtaddress);
               btnupdate=(Button) findViewById(R.id.btnupdate);

            Query get = FirebaseDatabase.getInstance().getReference("customer_details").orderByChild("cusemail").equalTo(esource);

            get.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for(DataSnapshot ds: dataSnapshot.getChildren()){
                        keys=ds.getKey();
                        String getname=ds.child("cusname").getValue().toString();
                        String getnic=ds.child("cusnic").getValue().toString();
                        String getemail=ds.child("cusemail").getValue().toString();
                        String getcontact=ds.child("cuscontact").getValue().toString();
                        String getaddress=ds.child("cusaddress").getValue().toString();


                        name.setText(getname);
                        nic.setText(getnic);
                        email.setText(getemail);
                        contact.setText(getcontact);
                        address.setText(getaddress);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            pDialog.dismissWithAnimation();

    }
        catch (Exception e)
    {
        Toast.makeText(frmUserEdit.this, e.getMessage(), Toast.LENGTH_SHORT).show();
    }

    }

    //validation
    public void validate(){
        btnupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

        if(! isNameValid(name.getText().toString()) ){
            Toast.makeText(frmUserEdit.this,"Invalid name",Toast.LENGTH_SHORT).show();
        }
        else if(! isNicValid(nic.getText().toString())||nic.getText().toString().equals("")){
            Toast.makeText(frmUserEdit.this,"Invalid nic",Toast.LENGTH_SHORT).show();

        }
        else if(address.getText().toString().equals("")){
            Toast.makeText(frmUserEdit.this,"Invalid Address",Toast.LENGTH_SHORT).show();

        }
        else if(! isEmailValid(email.getText().toString())){
            Toast.makeText(frmUserEdit.this,"Invalid email",Toast.LENGTH_SHORT).show();

        }
        else if(! isContactValid(contact.getText().toString())){
            Toast.makeText(frmUserEdit.this,"Invalid contact number",Toast.LENGTH_SHORT).show();

        }

        else {
            btnUpdate();

        }
    }
    });
    }


    //btn update
    public void btnUpdate(){


                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                user.updateEmail(email.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    reff= FirebaseDatabase.getInstance().getReference();

                                    reff.child("customer_details").child(keys).child("cusname").setValue(name.getText().toString());
                                    reff.child("customer_details").child(keys).child("cusnic").setValue(nic.getText().toString());
                                    reff.child("customer_details").child(keys).child("cusemail").setValue(email.getText().toString());
                                    reff.child("customer_details").child(keys).child("cuscontact").setValue(contact.getText().toString());
                                    reff.child("customer_details").child(keys).child("cusaddress").setValue(address.getText().toString());
                                    logoutAlert();
                                }
                                else{
                                    Toast.makeText(frmUserEdit.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();

                                }
                            }
                        });

    }



      //swetalert
      public void logoutAlert(){
          new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                  .setTitleText("Close the Tab?")
                  .setContentText("Successfully updated.Exit from here?")
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

        //regex
        public boolean isNameValid(String text){

            return text.matches("^([A-Za-z]+)(\\s[A-Za-z]+)*\\s?$");
        }

        public boolean isNicValid(String text){

        return text.matches("^((?:19|20)?\\d{2}(?:[01235678]\\d\\d(?<!(?:000|500|36[7-9]|3[7-9]\\d|86[7-9]|8[7-9]\\d)))\\d{4}(?i:v|x))||((?:19|20)?\\d{2}(?:[01235678]\\d\\d(?<!(?:000|500|36[7-9]|3[7-9]\\d|86[7-9]|8[7-9]\\d)))\\d{4}(?:[vVxX]))$");
        }

        public boolean isEmailValid(String text){
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(text).matches();
        }

        public boolean isContactValid(String text){

        return text.matches("^[0-9]{10}$");
        }


}
