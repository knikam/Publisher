package com.swami.kalpesh.publisher.Activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.admin.DeviceAdminInfo;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.swami.kalpesh.publisher.Model.PublicationModel;
import com.swami.kalpesh.publisher.Model.TeacherInfoModel;
import com.swami.kalpesh.publisher.R;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Add_PublicationActivity extends AppCompatActivity {

    /*
    * Component Declaration
    * */
    private EditText faculty_name;
    private EditText author_name;
    private EditText publication_type;
    private EditText paper_title;
    private EditText conference_name;
    private EditText publication_date;
    private EditText DOI;
    private EditText year_of_publication;
    private Button publicationBtn;

    /*
    * DataBase (Firebase Varibale)
    * */
    DatabaseReference databaseReference;

    /*
    * Global Variable Declaration
    * */
    String user;
    Timestamp timestamp;
    int day,month,year;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add__publication);
        getSupportActionBar().setTitle("Add Publication Detail");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        /*
        * Get Intent Received From Previous Activity
        * */
        user=getIntent().getStringExtra("username");

        /*
        * Initialized Firebase Location For Data Store
        * */
        databaseReference=FirebaseDatabase.getInstance().getReference("Publication_Detail");

        /*
        * Component initialization
        * */
        faculty_name=(EditText)findViewById(R.id.id_add_publication_facultyName);
        author_name=(EditText)findViewById(R.id.id_add_publication_authorName);
        publication_type=(EditText)findViewById(R.id.id_add_publication_publicationType);
        paper_title=(EditText)findViewById(R.id.id_add_publication_paperTitle);
        conference_name=(EditText)findViewById(R.id.id_add_publication_conferenceName);
        publication_date=(EditText)findViewById(R.id.id_add_publication_publicationDate);
        DOI=(EditText)findViewById(R.id.id_add_publication_DOI);
        year_of_publication=(EditText)findViewById(R.id.id_add_publication_publicationYear);
        publicationBtn=(Button)findViewById(R.id.id_add_publicationBtn);


        /*
        * All type Action Listener
        * */

        //Select Date from Datepicker Edittext
//        publication_date.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View view, boolean b) {
//                if(b)
//                {
//                    final Calendar calendar = Calendar.getInstance();
//                    day = calendar.get(Calendar.DAY_OF_MONTH);
//                    month = calendar.get(Calendar.MONTH);
//                    year = calendar.get(Calendar.YEAR);
//
//                    DatePickerDialog datePickerDialog=new DatePickerDialog(Add_PublicationActivity.this, new DatePickerDialog.OnDateSetListener() {
//                        @Override
//                        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
//                            int r_month=Integer.parseInt(String.valueOf(month));
//                            r_month=r_month+1;
//                            publication_date.setText(day+"-"+r_month+"-"+year);
//
//                            if(r_month<=5)
//                            {
//                                int next_year= Integer.parseInt(String.valueOf(year).substring(2));
//                                int current_year=Integer.parseInt(String.valueOf(year));
//                                current_year=current_year-1;
//                                year_of_publication.setText(current_year+"-"+next_year);
//                            }
//                            else
//                            {
//                                int next_year= Integer.parseInt(String.valueOf(year).substring(2));
//                                next_year=next_year+1;
//                                year_of_publication.setText(year+"-"+next_year);
//                            }
//
//                        }
//                    },year,month,day);
//
//                    datePickerDialog.show();
//                }
//            }
//        });

        //Funcation Written Bellow
        find_faculty();

        //Submit Data to Database Button
        publicationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Insert Data Function
                Insert_Data_In_Database();
            }
        });
    }


    /*
    * Insert Data To firebase Function
    * */
    public void Insert_Data_In_Database(){

        //check validation
        if(!Validation())
        {
            return;
        }

        //Show progress bar for Data store
        final ProgressDialog progressDialog=new ProgressDialog(Add_PublicationActivity.this);
        progressDialog.setTitle("Publication Detail");
        progressDialog.setMessage("Please Wait");
        progressDialog.setCancelable(false);
        progressDialog.show();

        //Generate key to insert data
        user=user.toLowerCase();
        int index=user.indexOf("@");
        final String childkey=user.substring(0,index);

        //Generate TimeStamp From Date
//        try {
//            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd-MM-yyyy");
//            Date date=simpleDateFormat.parse(publication_date.getText().toString());
//            timestamp=new Timestamp(date.getTime());
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }

        //Inset Data in Firebase
        final DatabaseReference reference=databaseReference.child(childkey).child(paper_title.getText().toString());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                PublicationModel publicationModel=new PublicationModel();
                publicationModel.setName_of_faculy(faculty_name.getText().toString());
                publicationModel.setName_of_author(author_name.getText().toString());
                publicationModel.setType_of_publication(publication_type.getText().toString());
                publicationModel.setTitle_of_paper(paper_title.getText().toString());
                publicationModel.setName_of_conference(conference_name.getText().toString());
                publicationModel.setDate_of_publication(publication_date.getText().toString());
                publicationModel.setDOI(DOI.getText().toString());
                publicationModel.setYear_of_publication(year_of_publication.getText().toString());
                publicationModel.setEmailid(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                reference.setValue(publicationModel);
                Toast.makeText(Add_PublicationActivity.this, "Publication Detail Added", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                finish();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Snackbar.make(findViewById(R.id.publication_activity),"Fail To Add",Snackbar.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }

        });
    }

    /*
    * Find and set Faculty Name
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
    * Back Arrow Return Funcation
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
    * Form Validation Funcation
    * */
    public boolean Validation()
    {
        if(TextUtils.isEmpty(faculty_name.getText().toString()))
        {
            faculty_name.setError("Faculty Name");
            return false;
        }
        else if(TextUtils.isEmpty(author_name.getText().toString()))
        {
            author_name.setError("Author Name");
            return false;
        }
        else if(TextUtils.isEmpty(publication_type.getText().toString()))
        {
            publication_type.setError("Publication Type");
            return false;
        }
        else if(TextUtils.isEmpty(paper_title.getText().toString()))
        {
            paper_title.setError("Paper Title");
            return false;
        }
        else if(TextUtils.isEmpty(conference_name.getText().toString()))
        {
            conference_name.setError("Conference Name");
            return false;
        }
        else if(TextUtils.isEmpty(publication_date.getText().toString()))
        {
            publication_date.setError("Publication Date");
            return false;
        }
        else if(TextUtils.isEmpty(DOI.getText().toString()))
        {
            DOI.setError("DOI");
            return false;
        }
        else if(TextUtils.isEmpty(year_of_publication.getText().toString()))
        {
            year_of_publication.setError("Acadmic Year");
            return false;
        }
        else  if(paper_title.getText().toString().contains(".")||paper_title.getText().toString().contains("$")||paper_title.getText().toString().contains("[")
                ||paper_title.getText().toString().contains("]")||paper_title.getText().toString().contains("#"))
        {
            Snackbar.make(findViewById(R.id.add_publication),"Paper title not Contain  . $ [ ] #",Snackbar.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


}
