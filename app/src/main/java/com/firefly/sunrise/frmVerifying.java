package com.firefly.sunrise;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.database.sqlite.SQLiteDatabase;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class frmVerifying extends AppCompatActivity {

    private TextView countdown;
    private TextView countdownlabel;

    private EditText key;
    private Button verifybtn;

    String name,nic,email,contact,address,pwd,rate="stillnot",comment="stillnot";
    String randomCode,rerandomCode;

    int c=0;
    int a=0;

    long maxid=0;
    DatabaseReference reff;
    Regclass regclass;

    FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frm_verifying);

        firebaseAuth=FirebaseAuth.getInstance();

        recivedData();
        startupDb();
        timer();
        btn_verify();

    }

    public void recivedData(){

        Bundle bundle = getIntent().getExtras();

         name = bundle.getString("pname");
         nic = bundle.getString("pnic");
         address = bundle.getString("paddress");
         email = bundle.getString("pmail");
         contact = bundle.getString("pcontact");
         pwd = bundle.getString("ppwd");

        randomCode= bundle.getString("rcode");


    }

    public void startupDb(){
        regclass = new Regclass();

        reff = FirebaseDatabase.getInstance().getReference().child("customer_details");

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

    public void timer(){

        countdown=findViewById(R.id.txttimer);
        countdownlabel=findViewById(R.id.txttimerremaning);



            new CountDownTimer(60000, 1000) {

                public void onTick(long millisUntilFinished) {
                    countdown.setText(millisUntilFinished / 1000 + " sec");
                    countdownlabel.setText("Time remainig");
                    //here you can have your logic to set text to edittext
                }

                public void onFinish() {

                    if (c == 1) {
                      //  countdown.setText("Try again later");
                        countdownlabel.setText("");
                        countdown.setText("");
                        Toast.makeText(frmVerifying.this, "please try again later", Toast.LENGTH_SHORT).show();



                    } else {
                        countdown.setText("Resend");
                        countdownlabel.setText("");

                        countdown.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //Toast.makeText(frmVerifying.this,"resend",Toast.LENGTH_SHORT).show();

                                if (c == 1) {
                                   // countdown.setText("");
                                    Toast.makeText(frmVerifying.this, "please try again later", Toast.LENGTH_SHORT).show();
                                } else {
                                    create_algorythm();
                                    sendMail();
                                    timer();
                                }
                            }
                        });
                    }

                }}.start();
        }


    public void create_algorythm(){
        randomCode.equals(null);
        SecureRandom random = new SecureRandom();
        randomCode = new BigInteger(30, random).toString(32).toUpperCase();

    }

    public void sendMail(){
        String mail=email;
        String message="Your new verification code is " +randomCode +". Don't share this code with others.";
        String subject="Verification";
        JavaMailAPI javaMailAPI=new JavaMailAPI(this ,mail,subject,message);
        javaMailAPI.execute();
        c++;

    }

    public void btn_verify(){

        key=(EditText) findViewById(R.id.txtverifykey);
        verifybtn=(Button)findViewById(R.id.btnverify);

        verifybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!isKeyValid(key.getText().toString())  ){

                    Toast.makeText(frmVerifying.this,"please insert correct format",Toast.LENGTH_SHORT).show();
                }
                else if(!key.getText().toString().equals(randomCode) ){

                    Toast.makeText(frmVerifying.this,"invalid code",Toast.LENGTH_SHORT).show();

                }
                else if(a==1){

                    Toast.makeText(frmVerifying.this,"please fill the fields again",Toast.LENGTH_SHORT).show();

                }
                else{

                  try{

                     // Date c = Calendar.getInstance().getTime();
                    //  SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
                    //  String formattedDate = df.format(c);

                     //  SQLiteDatabase mydb = openOrCreateDatabase("sunrise", MODE_PRIVATE, null);
                     //  mydb.execSQL("create table if not exists customer_details(cusid INTEGER primary key autoincrement,cusregdate,cusname varchar not null,cusnic varchar not null,cusemail varchar not null,cuscontact varchar not null,cususername varchar not null,cuspwd varchar not null,cusrate varchar,cuscomment varchar)");
                    //   mydb.execSQL("insert into customer_details values(null,'"+formattedDate+"','" + name+ "','" +nic+"','"+email+"','"+contact+"','"+username+"','"+pwd+"','"+rate+"','"+comment+"')");
                      // Toast.makeText(frmVerifying.this,"Get ready",Toast.LENGTH_SHORT).show();

                      insert();
                      a++;





                  }

                  catch(Exception e){
                      Toast.makeText(frmVerifying.this,e.getMessage(),Toast.LENGTH_SHORT).show();

                  }

                }

            }
        });
    }


    public void insert(){

        try {

    Date c = Calendar.getInstance().getTime();
    SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
    String formattedDate = df.format(c);

    String rate = "stillnot";
    String comment = "stillnot";
    String logstatus = "logedin";

    regclass.setCusregdate(formattedDate);
    regclass.setCusname(name);
    regclass.setCusnic(nic);
    regclass.setCusaddress(address);
    regclass.setCusemail(email);
    regclass.setCuscontact(contact);

    regclass.setCuspassword(pwd);
    regclass.setCusrate(rate);
    regclass.setCuscomment(comment);
    regclass.setCuslogstatus(logstatus);

    reff.child(String.valueOf(maxid + 1)).setValue(regclass);

    firebaseAuth.createUserWithEmailAndPassword(email,pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            if(task.isSuccessful()){

                callNext();
            }
            else{
                Toast.makeText(frmVerifying.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();

            }
        }
    });


}
catch(Exception e){
    Toast.makeText(frmVerifying.this, e.getMessage(), Toast.LENGTH_SHORT).show();

}

    }



    public void callNext(){
        Intent intent=new Intent(".frmHome");
        startActivity(intent);
        finish();
    }


    public boolean isKeyValid(String text){

        return text.matches("^[a-zA-Z0-9]{6}$");
    }
}
