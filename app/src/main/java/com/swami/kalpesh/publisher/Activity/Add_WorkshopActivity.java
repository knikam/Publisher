package com.swami.kalpesh.publisher.Activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.swami.kalpesh.publisher.Adapter.WorkshopAdapter;
import com.swami.kalpesh.publisher.Model.TeacherInfoModel;
import com.swami.kalpesh.publisher.Model.WorkshopModel;
import com.swami.kalpesh.publisher.R;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Add_WorkshopActivity extends AppCompatActivity {

    /*
    * Component Declaration
    * */
    private EditText faculty_name;
    private EditText workshop_name;
    private EditText oraganized_by;
    private EditText duration;
    private EditText start_date;
    private EditText end_date;
    private EditText acadmic_year;
    Button submitbtn;

    /*
    * Firebase Variable Declaration
    * */
    DatabaseReference databaseReference;

    /*
    * Global variable Declaration
    * */
    String user;
    Timestamp timestamp;
    int day,month,year;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add__workshop);
        getSupportActionBar().setTitle("Add Workshop Detail");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /*
        * Get Intend From Previous Activity
        * */
        user=getIntent().getStringExtra("username");

        /*
        * Firebase Location Initialization
        * */
        databaseReference= FirebaseDatabase.getInstance().getReference("Workshop_Detail");

        /*
        * Component Initialization
        * */
        faculty_name=(EditText)findViewById(R.id.id_add_workshop_faculty_name);
        workshop_name=(EditText)findViewById(R.id.id_add_workshop_name);
        oraganized_by=(EditText)findViewById(R.id.id_add_workshop_organize);
        duration=(EditText)findViewById(R.id.id_add_workshop_duration);
        start_date=(EditText)findViewById(R.id.id_add_workshop_startDate);
        end_date=(EditText)findViewById(R.id.id_add_workshop_endDate);
        acadmic_year=(EditText)findViewById(R.id.id_add_workshop_acadmicYear);
        submitbtn=(Button)findViewById(R.id.id_add_workshop_submitBtn);

        /*
        * All Activity Action Listener
        * */

        //start date datepicker Edittext Actionlistener
        start_date.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b)
                {
                    final Calendar calendar = Calendar.getInstance();
                    day = calendar.get(Calendar.DAY_OF_MONTH);
                    month = calendar.get(Calendar.MONTH);
                    year = calendar.get(Calendar.YEAR);

                    DatePickerDialog datePickerDialog=new DatePickerDialog(Add_WorkshopActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                            int r_month=Integer.parseInt(String.valueOf(month));
                            r_month=r_month+1;
                            start_date.setText(day+"-"+(String.valueOf(r_month))+"-"+year);

                            if(r_month<=5)
                            {
                                int next_year= Integer.parseInt(String.valueOf(year).substring(2));
                                int current_year=Integer.parseInt(String.valueOf(year));
                                current_year=current_year-1;
                                acadmic_year.setText(current_year+"-"+String.valueOf(next_year));
                            }
                            else
                            {
                                int next_year= Integer.parseInt(String.valueOf(year).substring(2));
                                next_year=next_year+1;
                                acadmic_year.setText(year+"-"+String.valueOf(next_year));
                            }

                        }
                    },year,month,day);

                    datePickerDialog.show();
                }
            }
        });

        //End date datepicker Edittext ActionListener
        end_date.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
               if(b)
               {
                   final Calendar calendar = Calendar.getInstance();
                   day = calendar.get(Calendar.DAY_OF_MONTH);
                   month = calendar.get(Calendar.MONTH);
                   year = calendar.get(Calendar.YEAR);

                   DatePickerDialog datePickerDialog=new DatePickerDialog(Add_WorkshopActivity.this, new DatePickerDialog.OnDateSetListener() {
                       @Override
                       public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                           int r_month=Integer.parseInt(String.valueOf(month));
                           r_month=r_month+1;
                           end_date.setText(day+"-"+(String.valueOf(r_month))+"-"+year);
                       }
                   },year,month,day);

                   datePickerDialog.show();
               }
            }
        });


        //Set Faculty name function below

        find_faculty();

        // Submit Button action Listener
        submitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                * Insert Data
                * */
                Insert_Data_In_Database();
            }
        });
    }


    /*
    * Insert Data To FireBase Funcation
    * */
    public void Insert_Data_In_Database(){

        if(!Validation())
        {
            return;
        }
        //Progress Bar to Sent Data
        final ProgressDialog progressDialog=new ProgressDialog(Add_WorkshopActivity.this);
        progressDialog.setTitle("Workshop Detail");
        progressDialog.setMessage("Please Wait");
        progressDialog.setCancelable(false);
        progressDialog.show();

        //Covert date to Timestamp
        try {
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd-MM-yyyy");
            Date date=simpleDateFormat.parse(start_date.getText().toString());
            timestamp=new Timestamp(date.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //Genrate Key From Email Id

        user=user.toLowerCase();
        int index=user.indexOf("@");
        final String childkey=user.substring(0,index);

        final DatabaseReference ref=databaseReference.child(childkey);
        final DatabaseReference reference=ref.child(workshop_name.getText().toString());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                WorkshopModel workshopModel=new WorkshopModel();
                workshopModel.setName_of_workshop(workshop_name.getText().toString());
                workshopModel.setOrganized_by(oraganized_by.getText().toString());
                workshopModel.setDuration(duration.getText().toString());
                workshopModel.setStart_date(start_date.getText().toString());
                workshopModel.setEnd_date(end_date.getText().toString());
                workshopModel.setAcadmic_year(acadmic_year.getText().toString());
                workshopModel.setTimestamp(timestamp.getTime());
                workshopModel.setFaculty_name(faculty_name.getText().toString());
                workshopModel.setEmailid(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                reference.setValue(workshopModel);
                progressDialog.dismiss();
                Toast.makeText(Add_WorkshopActivity.this, "Workshop Detail Added.", Toast.LENGTH_SHORT).show();
                finish();


            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Snackbar.make(findViewById(R.id.add_workshop),"Fail to Add",Snackbar.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });


        final DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Active_users").child(childkey);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                    TeacherInfoModel teacherInfoModel=dataSnapshot.getValue(TeacherInfoModel.class);
                    teacherInfoModel.setWorkshop(true);
                    databaseReference.setValue(teacherInfoModel);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    /*
    *  Find Faculty Name From Firebase.
    * */
    public void find_faculty()
    {
        user=user.toLowerCase();
        int index=user.indexOf("@");
        final String childkey=user.substring(0,index);

        DatabaseReference rootnode= FirebaseDatabase.getInstance().getReference("Active_users");

        rootnode.child(childkey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                TeacherInfoModel infoModel=dataSnapshot.getValue(TeacherInfoModel.class);
                faculty_name.setText(infoModel.getName());
                if(!TextUtils.isEmpty(faculty_name.getText().toString()))
                {
                    faculty_name.setEnabled(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /*
    * Back Arrow Return Function
    * */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onStart() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        super.onStart();
    }

    /*
    * Form Validation funcation
    * */
    public boolean Validation()
    {   if(TextUtils.isEmpty(faculty_name.getText().toString()))
        {
            faculty_name.setError("Faculty Name");
            return false;
        }
        else if(TextUtils.isEmpty(workshop_name.getText().toString()))
        {
            workshop_name.setError("Workshop Name");
            return false;
        }
        else if(TextUtils.isEmpty(oraganized_by.getText().toString()))
        {
            oraganized_by.setError("Organized By");
            return false;
        }
        else if(TextUtils.isEmpty(duration.getText().toString()))
        {
            duration.setError("Duration");
            return false;
        }
        else if(TextUtils.isEmpty(start_date.getText().toString()))
        {
            start_date.setError("Start Date");
            return false;
        }
        else if(TextUtils.isEmpty(end_date.getText().toString()))
        {
            end_date.setError("End Date");
            return false;
        }
        else if(TextUtils.isEmpty(acadmic_year.getText().toString()))
        {
            acadmic_year.setError("Academic Year");
            return false;
        }
        else  if(workshop_name.getText().toString().contains(".")||workshop_name.getText().toString().contains("$")||workshop_name.getText().toString().contains("[")
            ||workshop_name.getText().toString().contains("]")||workshop_name.getText().toString().contains("#"))
        {
            Snackbar.make(findViewById(R.id.add_workshop),"Workshop Name not Contain  . $ [ ] #",Snackbar.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
