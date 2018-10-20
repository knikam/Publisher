package com.swami.kalpesh.publisher.Activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.swami.kalpesh.publisher.Adapter.PublicationAdapter;
import com.swami.kalpesh.publisher.Adapter.WorkshopAdapter;
import com.swami.kalpesh.publisher.Classes.Hidekeyboard;
import com.swami.kalpesh.publisher.Model.PublicationModel;
import com.swami.kalpesh.publisher.Model.WorkshopModel;
import com.swami.kalpesh.publisher.R;

import org.w3c.dom.Text;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class ShowWorkshopActivity extends AppCompatActivity {

    String user;
    DatabaseReference databaseReference;
    Boolean isFABOpen=false;

    ShimmerFrameLayout shimmerFrameLayout;
    FloatingActionButton float_search_date,float_search_academic;
    TextView dates,academic;
    RecyclerView recyclerView;

    EditText searchbox,start_date,end_date;
    LinearLayout search_layout,date_search_layout;
    ImageView searchbtn,date_searchbtn;

    String search_key;

    Timestamp fromtimestamp,totimestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_workshop);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Workshop Report");
        user=getIntent().getStringExtra("username");

        if(!isInternetConnection())
        {
            final Snackbar snackbar=Snackbar.make(findViewById(R.id.show_workshop),"Internet Not Connect",Snackbar.LENGTH_LONG);
            snackbar.setAction("Dismiss", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    snackbar.dismiss();
                }
            });
            snackbar.show();
            return;
        }

        shimmerFrameLayout=findViewById(R.id.workshop_shimmer);
        search_layout=findViewById(R.id.id_workshop_searchbar);
        date_search_layout=findViewById(R.id.id_workshop_date_searchbar);


        FloatingActionButton floatingActionButton=findViewById(R.id.id_workshop_floatAction);
        float_search_date=findViewById(R.id.id_search_by_date);
        float_search_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                date_search_layout.setVisibility(View.VISIBLE);
                search_layout.setVisibility(View.GONE);
            }
        });
        float_search_academic=findViewById(R.id.id_search_by_acadmic);
        float_search_academic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search_layout.setVisibility(View.VISIBLE);
                date_search_layout.setVisibility(View.GONE);
            }
        });

        dates=findViewById(R.id.id_workshop_date_lable);
        academic=findViewById(R.id.id_workshop_academic_label);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isFABOpen)
                {
                    showFABMenu();
                    dates.setVisibility(View.VISIBLE);
                    academic.setVisibility(View.VISIBLE);
                    dates.animate().translationY(-110);
                    academic.animate().translationY(-230);

                }
                else
                {
                    closeFABMenu();
                    dates.animate().translationY(0);
                    academic.animate().translationY(0);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dates.setVisibility(View.GONE);
                            academic.setVisibility(View.GONE);
                        }
                    },250);
                    search_layout.setVisibility(View.GONE);
                    date_search_layout.setVisibility(View.GONE);
                }
            }
        });


        start_date=findViewById(R.id.id_workshop_fromdate_searchtxt);
        start_date.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b)
                {
                    int day,month,year;
                    final Calendar calendar = Calendar.getInstance();
                    day = calendar.get(Calendar.DAY_OF_MONTH);
                    month = calendar.get(Calendar.MONTH);
                    year = calendar.get(Calendar.YEAR);

                    DatePickerDialog datePickerDialog=new DatePickerDialog(ShowWorkshopActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                            int r_month=Integer.parseInt(String.valueOf(month));
                            r_month=r_month+1;
                            start_date.setText(day+"-"+String.valueOf(r_month)+"-"+year);
                        }
                    },year,month,day);

                    datePickerDialog.show();

                }
            }
        });


        end_date=findViewById(R.id.id_workshop_todate_searchtxt);
        end_date.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b)
                {
                    int day,month,year;
                    final Calendar calendar = Calendar.getInstance();
                    day = calendar.get(Calendar.DAY_OF_MONTH);
                    month = calendar.get(Calendar.MONTH);
                    year = calendar.get(Calendar.YEAR);

                    DatePickerDialog datePickerDialog=new DatePickerDialog(ShowWorkshopActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                            int r_month=Integer.parseInt(String.valueOf(month));
                            r_month=r_month+1;
                            end_date.setText(day+"-"+String.valueOf(r_month)+"-"+year);
                        }
                    },year,month,day);

                    datePickerDialog.show();

                }
            }
        });

        date_searchbtn=findViewById(R.id.id_workshop_date_searchbtn);
        date_searchbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Hidekeyboard.hideKeyboard(ShowWorkshopActivity.this);
                if(TextUtils.isEmpty(start_date.getText())||TextUtils.isEmpty(end_date.getText()))
                {
                    Snackbar.make(findViewById(R.id.show_workshop),"Select Date",Snackbar.LENGTH_SHORT).show();
                    return;
                }

                try {
                    SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd-MM-yyyy");
                    Date fromdate=simpleDateFormat.parse(start_date.getText().toString());
                    Date todate=simpleDateFormat.parse(end_date.getText().toString());
                    fromtimestamp=new Timestamp(fromdate.getTime());
                    totimestamp=new Timestamp(todate.getTime());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                //close float menu
                closeFABMenu();
                dates.animate().translationY(0);
                academic.animate().translationY(0);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dates.setVisibility(View.GONE);
                        academic.setVisibility(View.GONE);
                    }
                },250);
                date_search_layout.setVisibility(View.GONE);

                user=user.toLowerCase();
                int index=user.indexOf("@");
                final String childkey=user.substring(0,index);


                recyclerView.setVisibility(View.GONE);
                shimmerFrameLayout.setVisibility(View.VISIBLE);
                shimmerFrameLayout.startShimmerAnimation();
                databaseReference= FirebaseDatabase.getInstance().getReference("Workshop_Detail");
                DatabaseReference reference=databaseReference.child(childkey);
                reference.orderByChild("timestamp").startAt(fromtimestamp.getTime(),"timestamp").endAt(totimestamp.getTime(),"timestamp").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        ArrayList<WorkshopModel> workshopModelfilter=new ArrayList<>();
                        Iterable<DataSnapshot> ds=dataSnapshot.getChildren();

                        for(DataSnapshot child:ds)
                        {
                            workshopModelfilter.add(child.getValue(WorkshopModel.class));
                        }
                        Collections.reverse(workshopModelfilter);

                        recyclerView=findViewById(R.id.id_workshop_recycle);
                        WorkshopAdapter workshopAdapter=new WorkshopAdapter(workshopModelfilter,getApplicationContext());
                        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        recyclerView.setAdapter(workshopAdapter);
                        shimmerFrameLayout.stopShimmerAnimation();
                        shimmerFrameLayout.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });






        searchbtn=findViewById(R.id.id_workshop_searchbtn);
        searchbox=findViewById(R.id.id_workshop_acdemic_searchtxt);
        searchbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Hidekeyboard.hideKeyboard(ShowWorkshopActivity.this);
                if(TextUtils.isEmpty(searchbox.getText()))
                {
                    Snackbar.make(findViewById(R.id.show_workshop),"Enter Academic Year",Snackbar.LENGTH_SHORT).show();
                    return;
                }
                search_key=searchbox.getText().toString();

                closeFABMenu();
                dates.animate().translationY(0);
                academic.animate().translationY(0);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dates.setVisibility(View.GONE);
                        academic.setVisibility(View.GONE);
                    }
                },250);
                search_layout.setVisibility(View.GONE);

                recyclerView.setVisibility(View.GONE);
                shimmerFrameLayout.startShimmerAnimation();
                databaseReference= FirebaseDatabase.getInstance().getReference("Workshop_Detail");
                databaseReference.orderByChild("start_date").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ArrayList<WorkshopModel> workshopFilter=new ArrayList<>();
                        for(DataSnapshot ds:dataSnapshot.getChildren())
                        {
                            for(DataSnapshot child:ds.getChildren())
                            {
                                WorkshopModel workshop=child.getValue(WorkshopModel.class);
                                if(workshop.getAcadmic_year().equals(search_key))
                                {
                                    workshopFilter.add(workshop);
                                }
                            }
                        }

                        Collections.reverse(workshopFilter);

                        if(workshopFilter.isEmpty())
                        {
                            Snackbar.make(findViewById(R.id.show_workshop),"Data Not Found",Snackbar.LENGTH_SHORT).show();
                        }
                        recyclerView=findViewById(R.id.id_workshop_recycle);
                        WorkshopAdapter workshopAdapter=new WorkshopAdapter(workshopFilter,getApplicationContext());
                        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        recyclerView.setAdapter(workshopAdapter);
                        shimmerFrameLayout.stopShimmerAnimation();
                        shimmerFrameLayout.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });


        dump_date();
    }


    public void dump_date()
    {
        shimmerFrameLayout.startShimmerAnimation();
        user=user.toLowerCase();
        int index=user.indexOf("@");
        final String childkey=user.substring(0,index);

        databaseReference= FirebaseDatabase.getInstance().getReference("Workshop_Detail");
        DatabaseReference ref=databaseReference.child(childkey);
        ref.orderByChild("start_date").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<WorkshopModel> workshopModels=new ArrayList<>();
                Iterable<DataSnapshot> child=dataSnapshot.getChildren();
                for (DataSnapshot ds:child)
                {
                    workshopModels.add(ds.getValue(WorkshopModel.class));
                }
                Collections.reverse(workshopModels);

                recyclerView=findViewById(R.id.id_workshop_recycle);
                WorkshopAdapter workshopAdapter=new WorkshopAdapter(workshopModels,getApplicationContext());
                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                recyclerView.setAdapter(workshopAdapter);
                shimmerFrameLayout.stopShimmerAnimation();
                shimmerFrameLayout.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public  boolean isInternetConnection()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            return  true;
        }
        else {
            return false;
        }
    }

    private void showFABMenu(){
        isFABOpen=true;
        float_search_date.setVisibility(View.VISIBLE);
        float_search_academic.setVisibility(View.VISIBLE);
        float_search_date.animate().translationY(-120);
        float_search_academic.animate().translationY(-240);
    }

    private void closeFABMenu(){
        isFABOpen=false;
        float_search_date.animate().translationY(0);
        float_search_academic.animate().translationY(0);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                float_search_date.setVisibility(View.GONE);
                float_search_academic.setVisibility(View.GONE);
            }
        },250);
        date_search_layout.setVisibility(View.GONE);
        search_layout.setVisibility(View.GONE);
    }




    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        shimmerFrameLayout.startShimmerAnimation();
    }

    @Override
    protected void onPause() {
        shimmerFrameLayout.stopShimmerAnimation();
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if(!isFABOpen)
        {
            super.onBackPressed();
        }
        else
        {
            closeFABMenu();
            dates.animate().translationY(0);
            academic.animate().translationY(0);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dates.setVisibility(View.GONE);
                    academic.setVisibility(View.GONE);
                }
            },200);

            search_layout.setVisibility(View.GONE);
            date_search_layout.setVisibility(View.GONE);

        }
    }
}
