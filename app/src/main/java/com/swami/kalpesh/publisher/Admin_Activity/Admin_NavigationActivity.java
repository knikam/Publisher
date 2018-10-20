package com.swami.kalpesh.publisher.Admin_Activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.swami.kalpesh.publisher.R;

import java.util.ArrayList;

public class Admin_NavigationActivity extends AppCompatActivity {


    CardView request,workshop_attendace,faculty_per;
    PieChart pieChart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin__navigation);
        getSupportActionBar().setTitle("Admin Navigation");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        request=findViewById(R.id.id_admin_nav_requests);
        workshop_attendace=findViewById(R.id.id_admin_nav_workshop_att);
        faculty_per=findViewById(R.id.id_admin_nav_faculty_per);
        pieChart=findViewById(R.id.piechart);




        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),Admin_RequestActivity.class));
            }
        });

        workshop_attendace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    startActivity(new Intent(getApplicationContext(),Admin_Workshop_SeachActivity.class));
            }
        });

        faculty_per.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),Admin_faculty_PerformanceActivity.class));
            }
        });

        pieChart.setUsePercentValues(true);
        pieChart.setExtraOffsets(5,10,5,5);
        pieChart.setDragDecelerationFrictionCoef(0.9f);
        pieChart.setTransparentCircleRadius(61f);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setDescription("user");
        pieChart.animateY(1000);
        ArrayList<PieEntry> yValues = new ArrayList<>();
        yValues.add(new PieEntry(75,"Active"));
        yValues.add(new PieEntry(25,"Pending"));

        PieDataSet dataSet = new PieDataSet(yValues, "");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        PieData pieData = new PieData((dataSet));
        pieData.setValueTextSize(10f);
        pieData.setValueTextColor(Color.YELLOW);
        pieChart.setData(pieData);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
