package com.swami.kalpesh.publisher.Fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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


public class Non_Attend_listFragment extends Fragment {

    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    TeacherListAdapter teacherListAdapter;

    WorkshopModel workshopModel;
    ArrayList<TeacherInfoModel> list;

    String academic_year;
    int check=0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_non__attend_list, container, false);

        recyclerView=view.findViewById(R.id.workshop_non_att_list);

        Search_data();
        getData();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                teacherListAdapter = new TeacherListAdapter(list, getContext(), recyclerView);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.setAdapter(teacherListAdapter);
            }
        },500);

        return view;
    }

    public void getYear(String academic_year)
    {
        this.academic_year=academic_year;
    }

    public void Search_data()
    {

        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Workshop_Detail");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                list = new ArrayList<>();

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
                        String user = workshopModel.getEmailid().toLowerCase();
                        int index = user.indexOf("@");
                        final String childkey = user.substring(0, index);

                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Active_users").child(childkey);
                        databaseReference.orderByChild("name").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                TeacherInfoModel teacherInfoModel = dataSnapshot.getValue(TeacherInfoModel.class);
                                list.add(teacherInfoModel);

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
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
                       list.add(teacherInfoModel);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
