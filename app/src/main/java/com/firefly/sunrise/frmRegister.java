package com.firefly.sunrise;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.database.sqlite.SQLiteDatabase;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.regex.Pattern;

public class frmRegister extends AppCompatActivity {

    private EditText name;
    private EditText nic;
    private EditText address;
    private EditText email;
    private EditText contact;

    private EditText pwd;
    private EditText cpwd;
    private Button submit;

    String getname,getnic,getaddress,getemail,getcontact,getpwd,getcpwd;
    String randomCode;

    private Button testing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frm_register);
        getData();

    }

    public void getData(){
        name=(EditText) findViewById(R.id.txtname);
        nic=(EditText) findViewById(R.id.txtnic);
        address=(EditText) findViewById(R.id.txtaddress);
        email=(EditText) findViewById(R.id.txtemail);
        contact=(EditText) findViewById(R.id.txtcontact);
        pwd=(EditText) findViewById(R.id.txtpwd);
        cpwd=(EditText) findViewById(R.id.txtcpwd);
        submit=(Button) findViewById(R.id.btncreateaccount);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                validation();

            }
        });

    }

    //validate fields
    public void validation(){
        if(! isNameValid(name.getText().toString()) ){
            Toast.makeText(frmRegister.this,"Invalid name",Toast.LENGTH_SHORT).show();
        }
        else if(! isNicValid(nic.getText().toString())||nic.getText().toString().equals("")){
            Toast.makeText(frmRegister.this,"Invalid nic",Toast.LENGTH_SHORT).show();

        }
        else if(address.getText().toString().equals("")){
            Toast.makeText(frmRegister.this,"Invalid Address",Toast.LENGTH_SHORT).show();

        }
        else if(! isEmailValid(email.getText().toString())){
            Toast.makeText(frmRegister.this,"Invalid email",Toast.LENGTH_SHORT).show();

        }
        else if(! isContactValid(contact.getText().toString())){
            Toast.makeText(frmRegister.this,"Invalid contact number",Toast.LENGTH_SHORT).show();

        }
        else if( pwd.getText().toString().equals("") || Integer.parseInt(pwd.getText().toString())<6 ){
            Toast.makeText(frmRegister.this,"Request strong password",Toast.LENGTH_SHORT).show();

        }
        else if( cpwd.getText().toString().equals("")){
            Toast.makeText(frmRegister.this,"Request comfirm password",Toast.LENGTH_SHORT).show();

        }
        else if( !cpwd.getText().toString().equals(pwd.getText().toString())){
            Toast.makeText(frmRegister.this,"Password didn't match",Toast.LENGTH_SHORT).show();

        }
        else{

            verifyNic();

        }

    }

    public void valueAssign(){

        getname=name.getText().toString();
        getnic=nic.getText().toString();
        getaddress=address.getText().toString();
        getemail=email.getText().toString();
        getcontact=contact.getText().toString();
        getpwd=pwd.getText().toString();
        getcpwd=cpwd.getText().toString();
    }

    public void create_algorythm(){

        SecureRandom random = new SecureRandom();
        randomCode = new BigInteger(30, random).toString(32).toUpperCase();

    }

    public void sendMail(){
        String mail=getemail;
        String message="Your verification code is " +randomCode +". Don't share this code with others.";
        String subject="Verification";
        JavaMailAPI javaMailAPI=new JavaMailAPI(this ,mail,subject,message);
        javaMailAPI.execute();
    }

    public void callNext(){
        Intent intent=new Intent(".frmVerifying");

        //pass values to next page
        Bundle bundle = new Bundle();
        bundle.putString("pname", getname);
        bundle.putString("pnic",getnic);
        bundle.putString("paddress", getaddress);
        bundle.putString("pmail", getemail);
        bundle.putString("pcontact",getcontact);
        bundle.putString("ppwd",getpwd);
        bundle.putString("rcode", randomCode);
        intent.putExtras(bundle);
        startActivity(intent);
       // finish();

    }

    //verify nic
    public void verifyNic(){
        try {

            DatabaseReference reff= FirebaseDatabase.getInstance().getReference().child("customer_details");

            reff.orderByChild("cusnic").equalTo(nic.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        Toast.makeText(frmRegister.this,"Nic number already exhist",Toast.LENGTH_SHORT).show();

                    } else {

                        verifyEmail();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }
        catch (Exception e){
            Toast.makeText(frmRegister.this, e.getMessage(), Toast.LENGTH_SHORT).show();

        }
    }

    //verify email
    public void verifyEmail(){
        try {

            DatabaseReference reff= FirebaseDatabase.getInstance().getReference().child("customer_details");

            reff.orderByChild("cusemail").equalTo(email.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        Toast.makeText(frmRegister.this,"Email already exhist",Toast.LENGTH_SHORT).show();

                    } else {


                        valueAssign();
                        create_algorythm();
                        sendMail();
                        callNext();

                         }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
        catch (Exception e){
            Toast.makeText(frmRegister.this, e.getMessage(), Toast.LENGTH_SHORT).show();

        }
    }



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

    public boolean isUsernameValid(String text){

        return text.matches("^[a-z0-9_-]{3,15}$");
    }
    public boolean isPasswordValid(String text){

       // return text.matches("((?=.*[a-z])(?=.*\\\\d)(?=.*[A-Z])(?=.*[@#$%!]).{8,40})");
        return text.matches(" ^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\\\S+$).{4,}$");

    }
}
