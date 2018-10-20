package com.swami.kalpesh.publisher.Admin_Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.swami.kalpesh.publisher.Adapter.TeacherListAdapter;
import com.swami.kalpesh.publisher.Model.TeacherInfoModel;
import com.swami.kalpesh.publisher.Model.WorkshopModel;
import com.swami.kalpesh.publisher.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

public class Admin_Workshop_SeachActivity extends AppCompatActivity {

    EditText acdemic_year;
    Button Search_button;
    PieChart pieChart;

    int status=0;

    WorkshopModel workshopModel;

    int check=0;

    int Attend_count=0,non_attend_count=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin__workshop__seach);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Workshop Report");

        acdemic_year=findViewById(R.id.admin_year_text);
        Search_button=findViewById(R.id.admin_button);

        Search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(acdemic_year.getText().toString())) {
                    Toast.makeText(Admin_Workshop_SeachActivity.this, "Enter Academic Year", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent=new Intent(getApplicationContext(),Admin_Workshop_ReportActivity.class);
                intent.putExtra("year",acdemic_year.getText().toString());
                startActivity(intent);
                finish();
            }
        });

        pieChart=findViewById(R.id.search_piechart);

        Search_data();
        getData();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                pieChart.setExtraOffsets(5,10,5,5);
                pieChart.setDragDecelerationFrictionCoef(0.9f);
                pieChart.setTransparentCircleRadius(61f);
                pieChart.setHoleColor(Color.WHITE);
                pieChart.setDescription("Current Year Workshop Data");
                pieChart.animateY(1000, Easing.EasingOption.EaseInCubic);
                ArrayList<PieEntry> yValues = new ArrayList<>();
                yValues.add(new PieEntry(Attend_count,"Attend"));
                yValues.add(new PieEntry(non_attend_count,"Not Attend"));

                PieDataSet dataSet = new PieDataSet(yValues, "");
                dataSet.setSliceSpace(3f);
                dataSet.setSelectionShift(5f);
                dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                PieData pieData = new PieData((dataSet));
                pieData.setValueTextSize(10f);
                pieData.setValueTextColor(Color.YELLOW);
                pieChart.setData(pieData);
                //PieChart Ends Here

            }
        },1000);
    }


    public void Search_data()
    {
        int current_year= Calendar.getInstance().get(Calendar.YEAR);
        int next_year= Integer.parseInt(String.valueOf(current_year).substring(2));
        next_year=next_year+1;
        final String year=String.valueOf(current_year)+"-"+String.valueOf(next_year);
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Workshop_Detail");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot child:dataSnapshot.getChildren())
                {
                    status=0;
                    for(DataSnapshot workshop:child.getChildren())
                    {
                        workshopModel=workshop.getValue(WorkshopModel.class);

                        if(workshopModel.getAcadmic_year().equals(year) && status==0)
                        {
                            status=1;
                            Attend_count=Attend_count+1;
                            break;
                        }
                    }
                }

                for(DataSnapshot child:dataSnapshot.getChildren()) {
                    check=0;
                    for (DataSnapshot workshop : child.getChildren()) {
                        workshopModel=workshop.getValue(WorkshopModel.class);

                        if (workshopModel.getAcadmic_year().equals(year)) {
                            check = 1;
                            break;
                        }
                    }

                    if(!workshopModel.getAcadmic_year().equals(year) && check==0)
                    {
                        non_attend_count=non_attend_count+1;
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public void getData()
    {
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Active_users");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                    TeacherInfoModel teacherInfoModel=snapshot.getValue(TeacherInfoModel.class);
                    if(!teacherInfoModel.getWorkshop())
                    {
                        non_attend_count=non_attend_count+1;
                    }
                }

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

    @Override
    protected void onStart() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        super.onStart();
    }
}
