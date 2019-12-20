package com.firefly.sunrise;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class frmHome extends AppCompatActivity {

    String username;
    private CardView lo;
    private CardView btnrate;
    private CardView btnupdate;
    private CardView btnorder;
    private CardView btnview;


    String email;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frm_home);

        firebaseAuth=FirebaseAuth.getInstance();

        rate();
        logout();
        getEmail();
        updateAcc();
        order();
        view();
    }

    public void getEmail(){
        try {
            firebaseUser = firebaseAuth.getCurrentUser();
            email = firebaseUser.getEmail();
        }
        catch (Exception e)
        {
            Toast.makeText(frmHome.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    //callOrderForm
    public void order(){

        btnorder=(CardView) findViewById(R.id.btnorder);
        btnorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(frmHome.this,frmOrder.class));

            }
        });
    }

    //callRateForm
    public void rate(){

        btnrate=(CardView) findViewById(R.id.rate);
        btnrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(".frmRate");
                startActivity(intent);
            }
        });
    }
    public void view(){

        btnview=(CardView) findViewById(R.id.btnview);
        btnview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(frmHome.this,frmView.class));

            }
        });
    }

    //callUpdateForm
    public void updateAcc(){

        btnupdate=(CardView) findViewById(R.id.btnupdate);
        btnupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(frmHome.this,frmUserEdit.class));

            }
        });


    }
    public void logout(){
        lo=(CardView) findViewById(R.id.logout);

        lo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutAlert();
            }
        });

    }

    public void logoutAlert(){
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Log out")
                .setContentText("Log out of EatsAndTreats?")
                .setConfirmText("Logout").setConfirmButtonBackgroundColor(R.color.colorPrimary).setConfirmButtonTextColor(R.color.colorPrimary)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {

                       FirebaseAuth.getInstance().signOut();
                       Intent intent=new Intent(frmHome.this,MainActivity.class);
                       intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                       intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                       startActivity(intent);
                       finish();




                    }
                }).setCancelButtonBackgroundColor(R.color.colorPrimary).setCancelButtonTextColor(R.color.colorPrimary)
                .setCancelButton("Discard", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                    }
                })
                .show();
    }
}
