package com.firefly.sunrise;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class frmResetPassword extends AppCompatActivity {

    private EditText email;
    private Button btnverify;

    FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frm_reset_password);

        firebaseAuth=FirebaseAuth.getInstance();
        verify();
    }

    //verify
    public void verify() {
        try{
            email = (EditText) findViewById(R.id.txtemail);
            btnverify = (Button) findViewById(R.id.btnverify);




            btnverify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(email.getText().toString().equals("")){

                        Toast.makeText(frmResetPassword.this, "Fill the Email", Toast.LENGTH_SHORT).show();

                    }
                    else if(! isEmailValid(email.getText().toString())){
                        Toast.makeText(frmResetPassword.this, "Please enter an email correct format", Toast.LENGTH_SHORT).show();

                    }

                    else{

                        firebaseAuth.sendPasswordResetEmail(email.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                    sweetAlert();


                                } else {
                                    Toast.makeText(frmResetPassword.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();

                                }
                            }
                        });
                    }
                }
            });

        }
        catch(Exception exception){
            Toast.makeText(frmResetPassword.this,exception.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    //sweetAlert
    public void sweetAlert(){
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Password Reset")
                .setContentText("Check your emails and follow the given link.")
                .setConfirmText("Ok").setConfirmButtonBackgroundColor(R.color.colorPrimary).setConfirmButtonTextColor(R.color.colorPrimary)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        email.setText("");
                        finish();
                    }
                }).setCancelButtonBackgroundColor(R.color.colorPrimary).setCancelButtonTextColor(R.color.colorPrimary)

                .show();
    }

    //email validation
    public boolean isEmailValid(String text){
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(text).matches();
    }
}
