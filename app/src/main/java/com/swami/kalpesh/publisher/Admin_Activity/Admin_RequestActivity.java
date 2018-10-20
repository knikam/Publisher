package com.swami.kalpesh.publisher.Admin_Activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.WindowManager;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.swami.kalpesh.publisher.Adapter.UserAdapter;
import com.swami.kalpesh.publisher.Model.TeacherInfoModel;
import com.swami.kalpesh.publisher.R;

import java.util.ArrayList;

public class Admin_RequestActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private static boolean flag=false;
    UserAdapter userAdapter;

   private ShimmerFrameLayout shimmerFrameLayout;
   private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        getSupportActionBar().setTitle("Pending User");
        getSupportActionBar().setSubtitle("Allow to access Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        shimmerFrameLayout=findViewById(R.id.admin_shimmer);
        swipeRefreshLayout=findViewById(R.id.admin_refresh);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.themecolor));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                Load_User_Detail();
              swipeRefreshLayout.setRefreshing(false);
            }
        });

        Load_User_Detail();
    }

    private void Load_User_Detail() {

        shimmerFrameLayout.startShimmerAnimation();
        databaseReference= FirebaseDatabase.getInstance().getReference("VerificationData");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<TeacherInfoModel> teacherInfoModelArrayList=new ArrayList<>();
                Iterable<DataSnapshot> snapshots=dataSnapshot.getChildren();
                for(DataSnapshot child:snapshots)
                {
                    teacherInfoModelArrayList.add(child.getValue(TeacherInfoModel.class));
                }

                RecyclerView recyclerView=findViewById(R.id.id_user_recycle);
                userAdapter=new UserAdapter(teacherInfoModelArrayList,Admin_RequestActivity.this);
                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                recyclerView.setAdapter(userAdapter);
                shimmerFrameLayout.stopShimmerAnimation();
                shimmerFrameLayout.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //use the query to search
        }
    }

    /*-------------------------------------------------------------------------------------------------------
     *  Search Bar Action And Filter Data on Typing mode
     * -------------------------------------------------------------------------------------------------------*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        // Inflate menu to add items to action bar if it is present.
        inflater.inflate(R.menu.menu_main, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        //search view text listener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(userAdapter!=null)
                    userAdapter.getFilter().filter(newText);
                return true;
            }
        });
        return true;
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
    protected void onStart() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        super.onStart();
    }
}
