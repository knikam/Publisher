package com.swami.kalpesh.publisher.Fragment;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
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
import com.swami.kalpesh.publisher.Model.TeacherInfoModel;
import com.swami.kalpesh.publisher.Model.WorkshopModel;
import com.swami.kalpesh.publisher.R;

import java.util.ArrayList;
import java.util.Calendar;


public class Report_ChartFragment extends Fragment {

    String academic_year;
    PieChart pieChart;

    WorkshopModel workshopModel;

    int check=0;
    int status=0;
    int Attend_count=0,non_attend_count=0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_report__chart, container, false);
        pieChart=view.findViewById(R.id.report_chart);

        Search_data();
        getData();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                pieChart.setExtraOffsets(5,10,5,5);
                pieChart.setDragDecelerationFrictionCoef(0.9f);
                pieChart.setTransparentCircleRadius(61f);
                pieChart.setHoleColor(Color.WHITE);
                pieChart.setDescription(academic_year+"Workshop Data");
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

                non_attend_count=0;
                Attend_count=0;
            }
        },1000);
        return view;
    }

    public void getYear(String academic_year)
    {
        this.academic_year=academic_year;
    }

    public void Search_data()
    {

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

                        if(workshopModel.getAcadmic_year().equals(academic_year) && status==0)
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

                        if (workshopModel.getAcadmic_year().equals(academic_year)) {
                            check = 1;
                            break;
                        }
                    }

                    if(!workshopModel.getAcadmic_year().equals(academic_year) && check==0)
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

}
