package com.swami.kalpesh.publisher.Admin_Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.swami.kalpesh.publisher.Classes.Hidekeyboard;
import com.swami.kalpesh.publisher.Model.TeacherInfoModel;
import com.swami.kalpesh.publisher.Model.WorkshopModel;
import com.swami.kalpesh.publisher.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Admin_faculty_PerformanceActivity extends AppCompatActivity {

    Spinner faculty_name;
    EditText year;
    PieChart pieChart;
    Button search;
    TextView workshop,publication;

    HashMap<String,String> hashMap;
    List<String> name;

    int workshop_count=0;
    int publication_count=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_faculty__performance);
        getSupportActionBar().setTitle("Faculty Performance");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pieChart=findViewById(R.id.performace_chart);
        faculty_name=findViewById(R.id.faculty_list);
        year=findViewById(R.id.academic_year);
        workshop=findViewById(R.id.workshop_count);
        publication=findViewById(R.id.publication_count);
        search=findViewById(R.id.Search_btn);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Hidekeyboard.hideKeyboard(Admin_faculty_PerformanceActivity.this);

                if(TextUtils.isEmpty(year.getText().toString()))
                {
                    Toast.makeText(Admin_faculty_PerformanceActivity.this, "Enter Academic Year", Toast.LENGTH_SHORT).show();
                    return;
                }
                workshop_count=0;
                publication_count=0;
                String email=hashMap.get(faculty_name.getSelectedItem().toString());
                String academic=year.getText().toString();
                fetch_data(email,academic);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        pieChart.setUsePercentValues(true);
                        pieChart.setExtraOffsets(5,10,5,5);
                        pieChart.setDragDecelerationFrictionCoef(0.9f);
                        pieChart.setTransparentCircleRadius(61f);
                        pieChart.setHoleColor(Color.WHITE);
                        pieChart.setDescription("user");
                        pieChart.animateY(1000);
                        ArrayList<PieEntry> yValues = new ArrayList<>();
                        yValues.add(new PieEntry(Integer.parseInt(workshop.getText().toString()),"Workshop"));
                        yValues.add(new PieEntry(Integer.parseInt(publication.getText().toString()),"publication"));

                        PieDataSet dataSet = new PieDataSet(yValues, "");
                        dataSet.setSliceSpace(3f);
                        dataSet.setSelectionShift(5f);
                        dataSet.setColors(ColorTemplate.PASTEL_COLORS);
                        PieData pieData = new PieData((dataSet));
                        pieData.setValueTextSize(10f);
                        pieData.setValueTextColor(Color.YELLOW);
                        pieChart.setData(pieData);

                    }
                },1500);


            }
        });

        //default 0 chart
        pieChart.setUsePercentValues(true);
        pieChart.setExtraOffsets(5,10,5,5);
        pieChart.setDragDecelerationFrictionCoef(0.9f);
        pieChart.setTransparentCircleRadius(61f);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setDescription("user");
        pieChart.animateY(1000);
        ArrayList<PieEntry> yValues = new ArrayList<>();
        yValues.add(new PieEntry(0,"Active"));
        PieDataSet dataSet = new PieDataSet(yValues, "");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(ColorTemplate.PASTEL_COLORS);
        PieData pieData = new PieData((dataSet));
        pieData.setValueTextSize(10f);
        pieData.setValueTextColor(Color.YELLOW);
        pieChart.setData(pieData);

        getData();

    }


    public void fetch_data(String email, final String a_year)
    {
        final ProgressDialog progressDialog=new ProgressDialog(Admin_faculty_PerformanceActivity.this);
        progressDialog.show();
        String user=email.toLowerCase();
        int index=user.indexOf("@");
        final String childkey=user.substring(0,index);
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Workshop_Detail").child(childkey);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                    String year=snapshot.child("acadmic_year").getValue(String.class);
                    if(year.equals(a_year))
                    workshop_count=workshop_count+1;
                }
                workshop.setText(String.valueOf(workshop_count));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        DatabaseReference databaseReference1= FirebaseDatabase.getInstance().getReference("Publication_Detail").child(childkey);
        databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                    String year=snapshot.child("year_of_publication").getValue(String.class);
                    if(year.equals(a_year))
                    publication_count=publication_count+1;
                }
                publication.setText(String.valueOf(publication_count));
                progressDialog.dismiss();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    public void getData()
    {
       DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Active_users");
       databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(DataSnapshot dataSnapshot) {

               name=new LinkedList<>();
               hashMap=new HashMap<>();
               for(DataSnapshot snapshot:dataSnapshot.getChildren())
               {
                   TeacherInfoModel teacherInfoModel=snapshot.getValue(TeacherInfoModel.class);
                   hashMap.put(teacherInfoModel.getName(),teacherInfoModel.getEmail());
                   String fname=teacherInfoModel.getName();
                   name.add(fname);
               }

               ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(Admin_faculty_PerformanceActivity.this,R.layout.spinner_item,name);
               arrayAdapter.setDropDownViewResource(R.layout.spinner_drop_down);
               faculty_name.setAdapter(arrayAdapter);
           }

           @Override
           public void onCancelled(DatabaseError databaseError) {

           }
       });


    }

    @Override
    protected void onStart() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        super.onStart();
    }
}
