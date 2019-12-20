package com.firefly.sunrise;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private EditText email;
    private EditText pwd;
    private Button login;
    int attempt_counter=5;
    private TextView signup;

    private TextView forgotP;



    long maxid=0;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        email=(EditText) findViewById(R.id.txtemail);
        pwd=(EditText) findViewById(R.id.txtpwd);
        login=(Button) findViewById(R.id.btnlogin);

        firebaseAuth=FirebaseAuth.getInstance();

        FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
        FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();

        if(firebaseUser != null){
            startActivity(new Intent(MainActivity.this,frmHome.class));
            finish();
        }
        else{
            loginbutton();
            signupCall();
            resetPassword();

        }



    }

    public void loginHome(){
        firebaseAuth.signInWithEmailAndPassword(email.getText().toString(),pwd.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    Intent intent=new Intent(MainActivity.this,frmHome.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();

                }
                else{
                    Toast.makeText(MainActivity.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();

                }
            }
        });
    }

    //login button
    public void loginbutton(){
        email=(EditText) findViewById(R.id.txtemail);
        pwd=(EditText) findViewById(R.id.txtpwd);
        login=(Button) findViewById(R.id.btnlogin);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attempt_counter++;

               if(attempt_counter==0){
                        Toast.makeText(MainActivity.this,"You got maximum loggin limit.Try again later!",Toast.LENGTH_SHORT).show();
                        login.setEnabled(false);
               }
               else{

                   if(email.getText().toString().equals("")){

                       Toast.makeText(MainActivity.this, "Fill an Email", Toast.LENGTH_SHORT).show();

                   }
                   else if(! isEmailValid(email.getText().toString())){
                       Toast.makeText(MainActivity.this, "Please enter an email correct format", Toast.LENGTH_SHORT).show();

                   }
                   else if(pwd.getText().toString().equals("")){
                       Toast.makeText(MainActivity.this, "Fill password", Toast.LENGTH_SHORT).show();

                   }
                   else{
                       loginHome();

                   }

                   }
            }
        });

    }

    //verify username
    public void verifyUsername(){
        try {

            DatabaseReference reff= FirebaseDatabase.getInstance().getReference().child("customer_details");

            reff.orderByChild("cusemail").equalTo(email.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                       // loginHome();
                    } else {
                        Toast.makeText(MainActivity.this, "invalid Email", Toast.LENGTH_SHORT).show();

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }
        catch (Exception e){
            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

        }
    }


        //sign up textViewer
        public void signupCall() {
            signup = (TextView) findViewById(R.id.tvsignup);
            signup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(".frmRegister");
                    startActivity(intent);

                }
            });

        }

        //resetPasword
        public void resetPassword(){
            try{
                forgotP =  findViewById(R.id.tvsignup2);
                forgotP.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(MainActivity.this,frmResetPassword.class));

                    }
                });

            }
            catch (Exception e){
                Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();

            }
        }




    public boolean isUsernameValid(String text){

        return text.matches("^[a-z0-9_-]{3,15}$");
    }
    public boolean isPasswordValid(String text){

        // return text.matches("((?=.*[a-z])(?=.*\\\\d)(?=.*[A-Z])(?=.*[@#$%!]).{8,40})");
        return text.matches(" ^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\\\S+$).{4,}$");

    }
    public boolean isEmailValid(String text){
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(text).matches();
    }
}
