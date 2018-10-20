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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.swami.kalpesh.publisher.Adapter.PublicationAdapter;
import com.swami.kalpesh.publisher.Adapter.WorkshopAdapter;
import com.swami.kalpesh.publisher.Classes.Hidekeyboard;
import com.swami.kalpesh.publisher.Model.PublicationModel;
import com.swami.kalpesh.publisher.R;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

public class ShowPublicationActivity extends AppCompatActivity {

    DatabaseReference databaseReference;
    String username;
    boolean isFABOpen=false;

    ShimmerFrameLayout shimmerFrameLayout;
    FloatingActionButton float_search_academic;
    TextView academic;

    EditText searchbox;
    LinearLayout search_layout,date_search_layout;
    ImageView searchbtn;
    RecyclerView recyclerView;

    String search_key;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_publication);
        getSupportActionBar().setTitle("Publication Report");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        username=getIntent().getStringExtra("username");

        shimmerFrameLayout=findViewById(R.id.public_shimmer);
        search_layout=findViewById(R.id.id_publication_searchbar);

        //Internet Connection Message
        if(!isInternetConnection())
        {
            final Snackbar snackbar=Snackbar.make(findViewById(R.id.publication_activity),"Internet Not Connect",Snackbar.LENGTH_LONG);
            snackbar.setAction("Dismiss", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    snackbar.dismiss();
                }
            });
            snackbar.show();
        }

        /*==========================================================================================
         * Floating action Button
         ==========================================================================================*/

        FloatingActionButton floatingActionButton=findViewById(R.id.id_publication_floatAction);

        float_search_academic=findViewById(R.id.id_publication_academic);
        float_search_academic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               search_layout.setVisibility(View.VISIBLE);
            }
        });

        academic=findViewById(R.id.id_publication_academic_label);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isFABOpen)
                {
                    showFABMenu();
                    academic.setVisibility(View.VISIBLE);
                    academic.animate().translationY(-110);

                }
                else
                {
                    closeFABMenu();
                    academic.animate().translationY(0);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            academic.setVisibility(View.GONE);
                        }
                    },250);
                }
            }
        });




        /*==========================================================================================
         * Search Data from Database Through Academic Year
         ==========================================================================================*/

        searchbtn=findViewById(R.id.id_publication_searchbtn);
        searchbox=findViewById(R.id.id_publication_searchtxt);
        searchbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Hidekeyboard.hideKeyboard(ShowPublicationActivity.this);
                if(TextUtils.isEmpty(searchbox.getText()))
                {
                    Snackbar.make(findViewById(R.id.publication_activity),"Enter Academic Year",Snackbar.LENGTH_SHORT).show();
                    return;
                }

                search_key=searchbox.getText().toString();

                closeFABMenu();
                academic.animate().translationY(0);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        academic.setVisibility(View.GONE);
                    }
                },250);
                search_layout.setVisibility(View.GONE);

                recyclerView.setVisibility(View.GONE);
                shimmerFrameLayout.setVisibility(View.VISIBLE);
                shimmerFrameLayout.startShimmerAnimation();
                databaseReference= FirebaseDatabase.getInstance().getReference("Publication_Detail");
                databaseReference.orderByChild("date_of_publication").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        ArrayList<PublicationModel> publicationfilter=new ArrayList<>();
                        for (DataSnapshot ds:dataSnapshot.getChildren())
                        {
                            for(DataSnapshot child:ds.getChildren())
                            {
                                PublicationModel publication=child.getValue(PublicationModel.class);
                                if(publication.getYear_of_publication().equals(search_key))
                                {
                                    publicationfilter.add(publication);
                                }
                            }
                        }
                        Collections.reverse(publicationfilter);

                        recyclerView=findViewById(R.id.id_publication_recycle);
                        PublicationAdapter publicationAdapter=new PublicationAdapter(publicationfilter,getApplicationContext());
                        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        recyclerView.setAdapter(publicationAdapter);
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


        /*==========================================================================================
         * Load Data On Start Activity Funcation Call
         ==========================================================================================*/

            Load_start_data();
    }

    /*==========================================================================================
         * Load Data On Start Activity Funcation
     ==========================================================================================*/

    public void Load_start_data()
    {
        shimmerFrameLayout.startShimmerAnimation();

        username=username.toLowerCase();
        int index=username.indexOf("@");
        final String childkey=username.substring(0,index);

        databaseReference= FirebaseDatabase.getInstance().getReference("Publication_Detail");
        DatabaseReference reference=databaseReference.child(childkey);
        reference.orderByChild("date_of_publication").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ArrayList<PublicationModel> publicationModels=new ArrayList<>();
                Iterable<DataSnapshot> child=dataSnapshot.getChildren();
                for(DataSnapshot ds:child)
                {
                   publicationModels.add(ds.getValue(PublicationModel.class));
                }
                Collections.reverse(publicationModels);

                recyclerView=findViewById(R.id.id_publication_recycle);
                PublicationAdapter publicationAdapter=new PublicationAdapter(publicationModels,getApplicationContext());
                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                recyclerView.setAdapter(publicationAdapter);
               shimmerFrameLayout.stopShimmerAnimation();
               shimmerFrameLayout.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /*==========================================================================================
         * Check Internet Connectoion
      ==========================================================================================*/

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

    /*==========================================================================================
         * Float Action Button Menu Toggle Funcations
      ==========================================================================================*/
    private void showFABMenu(){
        isFABOpen=true;
        float_search_academic.setVisibility(View.VISIBLE);
        float_search_academic.animate().translationY(-120);
    }

    private void closeFABMenu(){
        isFABOpen=false;
        float_search_academic.animate().translationY(0);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                float_search_academic.setVisibility(View.GONE);
            }
        },250);

    }


    @Override
    protected void onPause() {
        shimmerFrameLayout.stopShimmerAnimation();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        shimmerFrameLayout.startShimmerAnimation();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
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
            academic.animate().translationY(0);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    academic.setVisibility(View.GONE);
                }
            },200);
        }
    }
}
