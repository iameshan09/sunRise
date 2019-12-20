package com.firefly.sunrise;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class frmOrder extends AppCompatActivity {
    private CarouselView carouselView;
    private int[] mImages=new int[]{
       R.drawable.image_chocolatecake,R.drawable.image_cupcake,R.drawable.image_fruitcake,R.drawable.image_gataeucake,R.drawable.image_vannilacake
    };
    private Spinner comboitemname;
    private ImageView imgdate;
    ValueEventListener listner;
    ArrayAdapter<String> adapter;
    ArrayList<String> spinnerdatalist;

    private Button btnverify;
    private Button btncheckavalability;


    int year_x,month_x,day_x;

    static  final  int DIALOG_ID=0;

    Date date,c;
    String ttt;

    private EditText orderdate;
    private EditText qty;
    private EditText dilivery;
    private Button btnnext;
    int price,discount;
    private EditText kg;


    String xorderdate,xitemtype,xdescription;
    int xqty,xkg;
    int getprice,getdiscount;

    DatabaseReference reff;
    Orders orders;
    String email;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frm_order);

        carouselView=(CarouselView)findViewById(R.id.carousel);
        carouselView.setPageCount(mImages.length);
        carouselView.setImageListener(new ImageListener() {
            @Override
            public void setImageForPosition(int position, ImageView imageView) {
                imageView.setImageResource(mImages[position]);
            }
        });

        final Calendar cal=Calendar.getInstance();
        year_x=cal.get(Calendar.YEAR);
        month_x=cal.get(Calendar.MONTH);
        day_x=cal.get(Calendar.DAY_OF_MONTH);
        firebaseAuth=FirebaseAuth.getInstance();


        identified();
        validation();
        getEmail();

    }
    public void getEmail(){
        try {
            firebaseUser = firebaseAuth.getCurrentUser();
            email = firebaseUser.getEmail();
        }
        catch (Exception e)
        {
            Toast.makeText(frmOrder.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    //identified
    public void identified(){

        orderdate=findViewById(R.id.txtorderdate);
        qty=findViewById(R.id.txtqty);
        dilivery=findViewById(R.id.txtdilivery);
        btnnext=findViewById(R.id.btnnext);
        kg=findViewById(R.id.txtkg);

        comboitemname = findViewById(R.id.comboname);



        orderdate.setVisibility(View.INVISIBLE);
        dilivery.setVisibility(View.INVISIBLE);
        qty.setVisibility(View.INVISIBLE);
        btnnext.setVisibility(View.INVISIBLE);
        kg.setVisibility(View.INVISIBLE);


        showDialog();



    }

    //showDialog
    public void showDialog(){
        imgdate=findViewById(R.id.imageView6);

        imgdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showDialog(DIALOG_ID);
            }
        });
    }


    //dialog
    @Override
    protected Dialog onCreateDialog(int id){
        if(id==DIALOG_ID)
            return new DatePickerDialog(this,dpickerListner,year_x,month_x,day_x);
            return  null;
    }

    //setListner
    private DatePickerDialog.OnDateSetListener dpickerListner = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

            year_x=year;
            month_x=month +1;
            day_x=dayOfMonth;

            Calendar calendar = Calendar.getInstance();
            calendar.clear();
            calendar.set(Calendar.MONTH, month_x-1);
            calendar.set(Calendar.YEAR, year_x);
            calendar.set(Calendar.DAY_OF_MONTH,day_x);

            date = calendar.getTime();



            SimpleDateFormat dfdf = new SimpleDateFormat("yyyy/MMM/dd");
            ttt = dfdf.format(date);

            c = Calendar.getInstance().getTime();
            SimpleDateFormat df = new SimpleDateFormat("yyyy/MMM/dd");
            String today = df.format(c);




            if(date.compareTo(c) == 0 || date.compareTo(c) < 0){
                Toast.makeText(frmOrder.this,"Please select future date",Toast.LENGTH_LONG).show();
                orderdate.setText("");
                qty.setText("");
                dilivery.setText("");
            }
            else{
                comfirmMessage();
            }


        }
    };


    //getdatafromotherdb
    public void getItems(){



          Query get = FirebaseDatabase.getInstance().getReference("Items");


        spinnerdatalist = new ArrayList<>();
        adapter=new ArrayAdapter<String>(frmOrder.this,android.R.layout.simple_spinner_dropdown_item,spinnerdatalist);

        comboitemname.setAdapter(adapter);

        listner=get.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot item:dataSnapshot.getChildren()){
                  //  spinnerdatalist.add(item.getValue().toString());

                   // spinnerdatalist.add(item.child("cusname").getValue().toString());
                    spinnerdatalist.add(item.child("name").getValue().toString());




                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    //sweetAlert
    public void comfirmMessage(){
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Avalable")
                .setContentText("We can provide your Item on that day.please fill the properties.")
                .setConfirmText("Accept").setConfirmButtonBackgroundColor(R.color.colorPrimary).setConfirmButtonTextColor(R.color.colorPrimary)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        orderdate.setVisibility(View.VISIBLE);
                        dilivery.setVisibility(View.VISIBLE);
                        qty.setVisibility(View.VISIBLE);
                        comboitemname.setVisibility(View.VISIBLE);

                        kg.setVisibility(View.VISIBLE);


                        btnnext.setVisibility(View.VISIBLE);

                        orderdate.setText(ttt);
                        getItems();

                        sDialog.dismissWithAnimation();



                    }
                }).setCancelButtonBackgroundColor(R.color.colorPrimary).setCancelButtonTextColor(R.color.colorPrimary)
                .setCancelButton("Ignore", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                    }
                })
                .show();
    }

    //validation
    public void validation(){

       btnnext.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if(orderdate.getText().toString().equals("") ){
                   Toast.makeText(frmOrder.this,"Select a date",Toast.LENGTH_SHORT).show();
               }
               else if(! isQuantityValid(kg.getText().toString())||kg.getText().toString().equals("") ||qty.getText().toString().equals("0")){
                   Toast.makeText(frmOrder.this,"Invalid type of kilogram",Toast.LENGTH_SHORT).show();

               }
               else if(! isQuantityValid(qty.getText().toString())||qty.getText().toString().equals("") ||qty.getText().toString().equals("0")){
                   Toast.makeText(frmOrder.this,"Invalid type of quantity",Toast.LENGTH_SHORT).show();

               }

               else{


                   xorderdate=orderdate.getText().toString();
                   xitemtype=comboitemname.getSelectedItem().toString();
                   xqty=Integer.parseInt(qty.getText().toString());
                   xdescription=dilivery.getText().toString();
                   xkg=Integer.parseInt(kg.getText().toString());
                   getPriceDiscount();
                   calculateBill();

               }
           }
       });





    }

    //retrivePriceDiscount
    public void getPriceDiscount(){
        try{

            Query get = FirebaseDatabase.getInstance().getReference("Items").orderByChild("name").equalTo(comboitemname.getSelectedItem().toString());

            get.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for(DataSnapshot ds: dataSnapshot.getChildren()){
                        getprice=Integer.parseInt(ds.child("price").getValue().toString());
                        getdiscount=Integer.parseInt(ds.child("discount").getValue().toString());

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });



        }
        catch (Exception e)
        {
            Toast.makeText(frmOrder.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    //calculateBill
    public void calculateBill(){

        int total=(getprice*xqty*xkg)-(getdiscount*xqty*xkg);

        if(total==0){
            Toast.makeText(frmOrder.this, "wait for the response and press next button again", Toast.LENGTH_SHORT).show();

        }
        else{
            Toast.makeText(frmOrder.this,String.valueOf(total),Toast.LENGTH_SHORT).show();

            Date c = Calendar.getInstance().getTime();
            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
            final String formattedDate = df.format(c);

            orders=new Orders();
            reff= FirebaseDatabase.getInstance().getReference().child("Orders");


            String tt=formattedDate.trim();
            String od=ttt.trim();
            String in=xitemtype.trim();
            int qt=xqty;
            int tot=total;
            String des=xdescription.trim();
            String ema=email.trim();
            orders.setKg(xkg);
            orders.setDate(tt);
            orders.setOrderdate(od);
            orders.setItemname(in);
            orders.setQty(xqty);
            orders.setTotal(total);
            orders.setDescription(des);
            orders.setEmail(email);

            reff.push().setValue(orders);


            String mail=email;
            String message="Your order is successflly.Please visit us on date and collect fresh product.Thank you!.";
            String subject="Verification";
            JavaMailAPI javaMailAPI=new JavaMailAPI(this ,mail,subject,message);
            javaMailAPI.execute();


            new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Thankyou")
                    .setContentText("Thank you for connect with us.")
                    .setConfirmText("Ok").setConfirmButtonBackgroundColor(R.color.colorPrimary).setConfirmButtonTextColor(R.color.colorPrimary)
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {

                            startActivity(new Intent(frmOrder.this,frmHome.class));
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

    //Regex
    public boolean isQuantityValid(String text){

        return text.matches("^[0-9]*$");
    }
}

