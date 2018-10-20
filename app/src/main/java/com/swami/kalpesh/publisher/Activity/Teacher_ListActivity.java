package com.swami.kalpesh.publisher.Activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.swami.kalpesh.publisher.Adapter.TeacherListAdapter;
import com.swami.kalpesh.publisher.Admin_Activity.Admin_LoginActivity;
import com.swami.kalpesh.publisher.Model.PublicationModel;
import com.swami.kalpesh.publisher.Model.TeacherInfoModel;
import com.swami.kalpesh.publisher.Model.WorkshopModel;
import com.swami.kalpesh.publisher.R;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Teacher_ListActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;

    FloatingActionButton floatingActionButton,add_profile,show_profile;
    MenuItem showuser;
    BottomSheetDialog container;
    BottomSheetDialog export_by_date,export_by_academic;

    EditText start_date,end_date;

    RecyclerView recyclerView;
    TeacherListAdapter teacherListAdapter;
    ArrayList<TeacherInfoModel> list;
    LinearLayoutManager linearLayoutManager;

    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    FirebaseUser current_user;

    ShimmerFrameLayout shimmerFrameLayout;
    SwipeRefreshLayout swipeRefreshLayout;

    String OldpostId;
    String FirstpostId;

    Date search_start_date = null;
    Date search_end_date=null;

    int Index;

    boolean float_boolean=false;

    Timestamp fromtimestamp,totimestamp;

    boolean doubleBackToExitPressedOnce = false;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_list);
        handleIntent(getIntent());
        getSupportActionBar().setTitle("Workshop & Publication");
        getSupportActionBar().setSubtitle("User Information");


        //Internet Connection Message
        if(!isInternetConnection())
        {

//             drawerLayout = (DrawerLayout) findViewById(R.id.activity_home);
//             LayoutInflater layoutInflater=(LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//             View view=layoutInflater.inflate(R.layout.no_internet_layout,null);
//            drawerLayout.addView(view);
            final Snackbar snackbar=Snackbar.make(findViewById(R.id.activity_home),"Internet Not Connect",Snackbar.LENGTH_LONG);
            snackbar.setAction("Dismiss", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    snackbar.dismiss();
                }
            });
            snackbar.show();
            return;
        }

        firebaseAuth=FirebaseAuth.getInstance();

        drawerLayout = (DrawerLayout) findViewById(R.id.activity_home);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        /* -----------------------------------------------------------------------------------------------
                Fetch Data From Fire Base Code
        --------------------------------------------------------------------------------------------------*/

        linearLayoutManager=new LinearLayoutManager(getApplicationContext());
        recyclerView=findViewById(R.id.id_teacher_list);

        shimmerFrameLayout=findViewById(R.id.shimmer_view);
        shimmerFrameLayout.startShimmerAnimation();
        databaseReference= FirebaseDatabase.getInstance().getReference("Active_users");
        databaseReference.orderByChild("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean flag=true;
                list = new ArrayList<>();
                Iterable<DataSnapshot> child=dataSnapshot.getChildren();
                for(DataSnapshot children:child)
                {

                    if(flag)
                    {
                        FirstpostId=children.getKey();
                        flag=false;
                    }
                    OldpostId=children.getKey();
                    list.add(children.getValue(TeacherInfoModel.class));
                }
                teacherListAdapter=new TeacherListAdapter(list,getApplicationContext(),recyclerView);
                recyclerView.setLayoutManager(linearLayoutManager);
                recyclerView.setAdapter(teacherListAdapter);
                shimmerFrameLayout.stopShimmerAnimation();
                shimmerFrameLayout.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        /*-------------------------------------------------------
         *   Swipe To Refresh Action
         * ---------------------------------------------------------*/
        swipeRefreshLayout=findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.themecolor));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                databaseReference= FirebaseDatabase.getInstance().getReference("Active_users");
                databaseReference.orderByChild("name").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        ArrayList<TeacherInfoModel> list = new ArrayList<>();

                        Iterable<DataSnapshot> child=dataSnapshot.getChildren();
                        for(DataSnapshot children:child)
                        {
                            list.add(children.getValue(TeacherInfoModel.class));
                        }

                        recyclerView=findViewById(R.id.id_teacher_list);
                        teacherListAdapter=new TeacherListAdapter(list,getApplicationContext(),recyclerView);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        recyclerView.setAdapter(teacherListAdapter);
                        shimmerFrameLayout.stopShimmerAnimation();
                        shimmerFrameLayout.setVisibility(View.GONE);
                        swipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });

        /*-------------------------------------------------------
        *   Float Button Action
        * ---------------------------------------------------------*/

        floatingActionButton=findViewById(R.id.id_floatAction);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getApplicationContext(),RegisterActivity.class));
            }
        });


         /* -----------------------------------------------------------------------------------------------
               Navigation Drawer Acces Code
        --------------------------------------------------------------------------------------------------*/

        NavigationView navigationView=(NavigationView)findViewById(R.id.id_menu);
        final Menu menu=navigationView.getMenu();
        showuser=menu.findItem(R.id.id_menu_login);
        if(firebaseAuth.getCurrentUser()!=null)
        {
            showuser.setTitle(firebaseAuth.getCurrentUser().getEmail());
        }
        else
        {
            showuser.setTitle("Log In");
        }
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId())
                {
                    case R.id.id_menu_login:
                        if(showuser.getTitle()=="Log In")
                        {
                            Intent intent=new Intent(getApplicationContext(),LoginActivity.class);
                            intent.putExtra("type","other");
                            startActivity(intent);
                        }
                        break;

                    case R.id.id_menu_profile:
                        if(FirebaseAuth.getInstance().getCurrentUser()!=null) {
                            startActivity(new Intent(getApplicationContext(), User_profileActivity.class));
                        }
                        else
                        {
                            Intent intent=new Intent(getApplicationContext(),LoginActivity.class);
                            intent.putExtra("type","other");
                            startActivity(intent);
                        }
                        break;
                    case R.id.id_menu_export_data:

                        if(FirebaseAuth.getInstance().getCurrentUser()==null)
                        {
                            Intent intent=new Intent(getApplicationContext(),LoginActivity.class);
                            intent.putExtra("type","other");
                            startActivity(intent);
                            Toast.makeText(Teacher_ListActivity.this, "You Must Log In", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            //funcation at bottom
                            Export_Data();
                        }
                        break;
                    case R.id.id_menu_changepassword:
                        //funcation at bottom
                        if(firebaseAuth.getCurrentUser()==null)
                        {
                            Intent intent=new Intent(getApplicationContext(),LoginActivity.class);
                            intent.putExtra("type","other");
                            startActivity(intent);
                            Toast.makeText(Teacher_ListActivity.this, "You Must Log In", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Change_Password();
                        }
                        break;
                    case R.id.id_menu_Admin:
                        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                        startActivity(new Intent(getApplicationContext(),Admin_LoginActivity.class));

                        break;
                    case R.id.id_menu_logout:
                            FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
                            firebaseAuth.signOut();
                            showuser.setTitle("Log In");
                            Toast.makeText(Teacher_ListActivity.this, "Logout", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.id_menu_about:
                        Toast.makeText(Teacher_ListActivity.this, "About", Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            }
        });

    }//end on create


     /* -----------------------------------------------------------------------------------------------
               Search Bar Result code
        --------------------------------------------------------------------------------------------------*/

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
                if(teacherListAdapter!=null)
                teacherListAdapter.getFilter().filter(newText);
                return true;
            }
        });
        return true;
    }

    /*-------------------------------------------------------------------
    * Back button And Menu Button Action
    * --------------------------------------------------------------------*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press again to Exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 1000);
    }



    /*-------------------------------------------------------------------------------
    *  System Life Cycle Method
    * --------------------------------------------------------------------------------*/

    @Override
    protected void onResume() {
        super.onResume();
        shimmerFrameLayout.startShimmerAnimation();
        if(isInternetConnection()) {
            if (firebaseAuth.getCurrentUser() != null) {
                showuser.setTitle(firebaseAuth.getCurrentUser().getEmail());
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onPause() {
        shimmerFrameLayout.stopShimmerAnimation();
        super.onPause();
    }




    /* -----------------------------------------------------------------------------------------------
                Drawer Menu Action Funcation
        --------------------------------------------------------------------------------------------------*/





    /*==============================================================================================
    *    Export Data from database to Excel sheet
    * ==============================================================================================*/

     public void Export_Data()
     {
         final BottomSheetDialog exportpop_up=new BottomSheetDialog(Teacher_ListActivity.this);
         exportpop_up.setContentView(R.layout.export_popup_layout);
         exportpop_up.show();


         //Workshop Data Button Action Listener

         final Button workshop_btn=exportpop_up.findViewById(R.id.id_export_workshop_btn);
         workshop_btn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(final View view) {


                 PopupMenu exportmenu =new PopupMenu(Teacher_ListActivity.this,view);
                 exportmenu.getMenuInflater().inflate(R.menu.export_menu_workshop,exportmenu.getMenu());
                 exportmenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                     @Override
                     public boolean onMenuItemClick(MenuItem menuItem) {


                         if(menuItem.getItemId()==R.id.menu_search_by_date)
                         {

                             exportpop_up.dismiss();
                             export_by_date=new BottomSheetDialog(Teacher_ListActivity.this);
                             export_by_date.setContentView(R.layout.export_by_date_layout);
                             export_by_date.show();
                             //Export By Date

                             start_date=export_by_date.findViewById(R.id.export_by_date_start_date);
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

                                         DatePickerDialog datePickerDialog=new DatePickerDialog(Teacher_ListActivity.this, new DatePickerDialog.OnDateSetListener() {
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


                             end_date=export_by_date.findViewById(R.id.export_by_date_end_date);
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

                                         DatePickerDialog datePickerDialog=new DatePickerDialog(Teacher_ListActivity.this, new DatePickerDialog.OnDateSetListener() {
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

                             Button export_by_date_btn=export_by_date.findViewById(R.id.export_by_date_button);
                             export_by_date_btn.setOnClickListener(new View.OnClickListener() {
                                 @Override
                                 public void onClick(View view) {

                                     if(TextUtils.isEmpty(start_date.getText().toString())||TextUtils.isEmpty(end_date.getText().toString()))
                                     {
                                         Toast.makeText(Teacher_ListActivity.this, "Select Date", Toast.LENGTH_SHORT).show();
                                         return;
                                     }

                                    export_workshop_data_by_date(start_date.getText().toString(),end_date.getText().toString());
                                     export_by_date.dismiss();

                                 }
                             });

                         }


                         else if(menuItem.getItemId()==R.id.menu_search_by_Academic)
                         {

                             exportpop_up.dismiss();
                              export_by_academic=new BottomSheetDialog(Teacher_ListActivity.this);
                             export_by_academic.setContentView(R.layout.export_by_acadmic_year_layout);
                             export_by_academic.show();

                             Button export_by_academic_btn=export_by_academic.findViewById(R.id.export_by_academic_btn);
                             final EditText export_by_academic_txt=export_by_academic.findViewById(R.id.export_by_academic);
                             export_by_academic_btn.setOnClickListener(new View.OnClickListener() {
                                 @Override
                                 public void onClick(View view) {

                                     if (TextUtils.isEmpty(export_by_academic_txt.getText().toString())) {
                                         Toast.makeText(Teacher_ListActivity.this, "Enter Academic Year", Toast.LENGTH_SHORT).show();
                                     }
                                     else
                                     {
                                         export_workshop_data_by_academic_year(export_by_academic_txt.getText().toString());
                                         export_by_academic.dismiss();
                                     }
                                 }
                             });
                         }

                         else
                         {
                             export_workshop_all_data();
                         }
                         return true;
                     }
                 });

                 exportmenu.show();
             }
         });



         //Export Publication Data Action Listener

         Button publication_btn=exportpop_up.findViewById(R.id.id_export_publication_btn);
         publication_btn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {

                 PopupMenu exportmenu =new PopupMenu(Teacher_ListActivity.this,view);
                 exportmenu.getMenuInflater().inflate(R.menu.export_menu_publication,exportmenu.getMenu());

                 exportmenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                     @Override
                     public boolean onMenuItemClick(MenuItem menuItem) {


                         if(menuItem.getItemId()==R.id.menu_publication_search_by_Academic)
                         {
                             exportpop_up.dismiss();
                             final BottomSheetDialog export_publication_by_academic=new BottomSheetDialog(Teacher_ListActivity.this);
                             export_publication_by_academic.setContentView(R.layout.export_by_acadmic_year_layout);
                             export_publication_by_academic.show();

                             Button export_by_academic_btn=export_publication_by_academic.findViewById(R.id.export_by_academic_btn);
                             final EditText export_by_academic_txt=export_publication_by_academic.findViewById(R.id.export_by_academic);
                             export_by_academic_btn.setOnClickListener(new View.OnClickListener() {
                                 @Override
                                 public void onClick(View view) {
                                     if(TextUtils.isEmpty(export_by_academic_txt.getText().toString()))
                                     {
                                         Toast.makeText(Teacher_ListActivity.this, "Enter Academic Year", Toast.LENGTH_SHORT).show();
                                     }
                                     else
                                     {
                                         export_publication_by_academic(export_by_academic_txt.getText().toString());
                                         export_publication_by_academic.dismiss();
                                     }

                                 }
                             });
                         }
                         else
                         {
                            export_all_publication_data();
                         }
                         return true;
                     }
                 });

                 exportmenu.show();
             }
         });
     }




     /*
     * ==========================================================================================================
     *                              Export All Workshop Data
     * ==========================================================================================================
     *
     * */



     public void export_workshop_all_data()
     {
         final ProgressDialog progressDialog=new ProgressDialog(Teacher_ListActivity.this);
         progressDialog.setTitle("Data Exporting");
         progressDialog.setMessage("Exporting...");
         progressDialog.show();
         DatabaseReference  databaseReference=FirebaseDatabase.getInstance().getReference();
         final DatabaseReference rootref=databaseReference.child("Workshop_Detail");

         ValueEventListener workshop_detail=new ValueEventListener() {
             @Override
             public void onDataChange(DataSnapshot dataSnapshot) {

                 HSSFWorkbook workshop_detail=new HSSFWorkbook();
                 HSSFSheet workshop_sheet=workshop_detail.createSheet("Workshop_deatail");
                 HSSFRow header=workshop_sheet.createRow(0);

                 header.createCell(0).setCellValue("Faculty Name");
                 header.createCell(1).setCellValue("Name of Workshop");
                 header.createCell(2).setCellValue("Organized By");
                 header.createCell(3).setCellValue("Duration");
                 header.createCell(4).setCellValue("Start Date");
                 header.createCell(5).setCellValue("End Date");
                 header.createCell(6).setCellValue("Academic Year");

                 workshop_sheet.setColumnWidth(0,100*75);
                 workshop_sheet.setColumnWidth(1,100*75);
                 workshop_sheet.setColumnWidth(2,100*50);
                 workshop_sheet.setColumnWidth(3,100*50);
                 workshop_sheet.setColumnWidth(4,100*50);
                 workshop_sheet.setColumnWidth(5,100*50);
                 workshop_sheet.setColumnWidth(6,100*50);

                 int index=1;

                 for(DataSnapshot ds:dataSnapshot.getChildren())
                 {
                     if(ds!=null)
                     {
                         for(DataSnapshot child:ds.getChildren())
                         {
                             WorkshopModel workshopModel=child.getValue(WorkshopModel.class);

                             HSSFRow row=workshop_sheet.createRow(index);

                             row.createCell(0).setCellValue(workshopModel.getFaculty_name());
                             row.createCell(1).setCellValue(workshopModel.getName_of_workshop());
                             row.createCell(2).setCellValue(workshopModel.getOrganized_by());
                             row.createCell(3).setCellValue(workshopModel.getDuration());
                             row.createCell(4).setCellValue(workshopModel.getStart_date());
                             row.createCell(5).setCellValue(workshopModel.getEnd_date());
                             row.createCell(6).setCellValue(workshopModel.getAcadmic_year());

                             index++;
                         }
                     }
                 }

                 try {

                     Context context=getApplicationContext();
                     File file=new File(context.getExternalFilesDir(null),"Workshop Detail.xls");
                     FileOutputStream fileOutputStream=new FileOutputStream(file);
                     workshop_detail.write(fileOutputStream);
                     progressDialog.dismiss();
                     Toast.makeText(Teacher_ListActivity.this, "Workshop Detail Export", Toast.LENGTH_SHORT).show();
                 }
                 catch (Exception ex)
                 {
                     Toast.makeText(Teacher_ListActivity.this, ex.toString(), Toast.LENGTH_SHORT).show();
                 }
             }
             @Override
             public void onCancelled(DatabaseError databaseError) {

             }
         };
         rootref.addListenerForSingleValueEvent(workshop_detail);
     }






     //Export Data By In Range Dates

     public void export_workshop_data_by_date(final String start_Date, String end_Date)
     {

         try {
             SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd-MM-yyyy");
             search_start_date=simpleDateFormat.parse(start_Date);
             search_end_date=simpleDateFormat.parse(end_Date);
             fromtimestamp=new Timestamp(search_start_date.getTime());
             totimestamp=new Timestamp(search_end_date.getTime());
         } catch (ParseException e) {
             e.printStackTrace();
         }


         final ProgressDialog progressDialog=new ProgressDialog(Teacher_ListActivity.this);
         progressDialog.setTitle("Data Exporting");
         progressDialog.setMessage("Exporting...");
         progressDialog.show();
         DatabaseReference  databaseReference=FirebaseDatabase.getInstance().getReference();
         final DatabaseReference rootref=databaseReference.child("Workshop_Detail");

         ValueEventListener workshop_detail=new ValueEventListener() {
             @Override
             public void onDataChange(DataSnapshot dataSnapshot) {

                 HSSFWorkbook workshop_detail=new HSSFWorkbook();
                 HSSFSheet workshop_sheet=workshop_detail.createSheet("Workshop_deatail");
                 HSSFRow header=workshop_sheet.createRow(0);

                 header.createCell(0).setCellValue("Faculty Name");
                 header.createCell(1).setCellValue("Name of Workshop");
                 header.createCell(2).setCellValue("Organized By");
                 header.createCell(3).setCellValue("Duration");
                 header.createCell(4).setCellValue("Start Date");
                 header.createCell(5).setCellValue("End Date");
                 header.createCell(6).setCellValue("Academic Year");

                 workshop_sheet.setColumnWidth(0,100*75);
                 workshop_sheet.setColumnWidth(1,100*75);
                 workshop_sheet.setColumnWidth(2,100*50);
                 workshop_sheet.setColumnWidth(3,100*50);
                 workshop_sheet.setColumnWidth(4,100*50);
                 workshop_sheet.setColumnWidth(5,100*50);
                 workshop_sheet.setColumnWidth(6,100*50);

                 int index=1;

                 for(DataSnapshot ds:dataSnapshot.getChildren())
                 {
                     if(ds!=null)
                     {
                         for(DataSnapshot child:ds.getChildren()) {

                             WorkshopModel workshopModel = child.getValue(WorkshopModel.class);


                                if(fromtimestamp.getTime() <= workshopModel.getTimestamp() && totimestamp.getTime() >= workshopModel.getTimestamp()) {

                                    HSSFRow row = workshop_sheet.createRow(index);
                                    row.createCell(0).setCellValue(workshopModel.getFaculty_name());
                                    row.createCell(1).setCellValue(workshopModel.getName_of_workshop());
                                    row.createCell(2).setCellValue(workshopModel.getOrganized_by());
                                    row.createCell(3).setCellValue(workshopModel.getDuration());
                                    row.createCell(4).setCellValue(workshopModel.getStart_date());
                                    row.createCell(5).setCellValue(workshopModel.getEnd_date());
                                    row.createCell(6).setCellValue(workshopModel.getAcadmic_year());

                                    index++;
                                }
                         }
                     }
                 }

                 try {

                     Context context=getApplicationContext();
                     File file=new File(context.getExternalFilesDir(null),"Workshop Detail by Date.xls");
                     FileOutputStream fileOutputStream=new FileOutputStream(file);
                     workshop_detail.write(fileOutputStream);
                     progressDialog.dismiss();
                     Toast.makeText(Teacher_ListActivity.this, "Workshop Detail Export", Toast.LENGTH_SHORT).show();
                 }
                 catch (Exception ex)
                 {
                     Toast.makeText(Teacher_ListActivity.this, ex.toString(), Toast.LENGTH_SHORT).show();
                 }
             }
             @Override
             public void onCancelled(DatabaseError databaseError) {

             }
         };
         rootref.addListenerForSingleValueEvent(workshop_detail);
     }




     /*=============================================================
     * Export Data By Academic Year
     * ==============================================================*/


     public void export_workshop_data_by_academic_year(final String Academic_year)
     {
         final ProgressDialog progressDialog=new ProgressDialog(Teacher_ListActivity.this);
         progressDialog.setTitle("Data Exporting");
         progressDialog.setMessage("Exporting...");
         progressDialog.show();
         DatabaseReference  databaseReference=FirebaseDatabase.getInstance().getReference();
         final DatabaseReference rootref=databaseReference.child("Workshop_Detail");

         ValueEventListener workshop_detail_by_year=new ValueEventListener() {
             @Override
             public void onDataChange(DataSnapshot dataSnapshot) {

                 HSSFWorkbook workshop_detail_by_year=new HSSFWorkbook();
                 HSSFSheet workshop_sheet=workshop_detail_by_year.createSheet("Workshop_deatail");
                 HSSFRow header=workshop_sheet.createRow(0);

                 header.createCell(0).setCellValue("Faculty Name");
                 header.createCell(1).setCellValue("Name of Workshop");
                 header.createCell(2).setCellValue("Organized By");
                 header.createCell(3).setCellValue("Duration");
                 header.createCell(4).setCellValue("Start Date");
                 header.createCell(5).setCellValue("End Date");
                 header.createCell(6).setCellValue("Academic Year");

                 workshop_sheet.setColumnWidth(0,100*75);
                 workshop_sheet.setColumnWidth(1,100*75);
                 workshop_sheet.setColumnWidth(2,100*50);
                 workshop_sheet.setColumnWidth(3,100*50);
                 workshop_sheet.setColumnWidth(4,100*50);
                 workshop_sheet.setColumnWidth(5,100*50);
                 workshop_sheet.setColumnWidth(6,100*50);

                 int index=1;

                 for(DataSnapshot ds:dataSnapshot.getChildren())
                 {
                     if(ds!=null)
                     {
                         for(DataSnapshot child:ds.getChildren())
                         {
                             WorkshopModel workshopModel=child.getValue(WorkshopModel.class);

                             if(workshopModel.getAcadmic_year().equals(Academic_year)) {

                                 HSSFRow row = workshop_sheet.createRow(index);

                                 row.createCell(0).setCellValue(workshopModel.getFaculty_name());
                                 row.createCell(1).setCellValue(workshopModel.getName_of_workshop());
                                 row.createCell(2).setCellValue(workshopModel.getOrganized_by());
                                 row.createCell(3).setCellValue(workshopModel.getDuration());
                                 row.createCell(4).setCellValue(workshopModel.getStart_date());
                                 row.createCell(5).setCellValue(workshopModel.getEnd_date());
                                 row.createCell(6).setCellValue(workshopModel.getAcadmic_year());

                                 index++;
                             }
                         }
                     }
                 }

                 try {

                     Context context=getApplicationContext();
                     File file=new File(context.getExternalFilesDir(null),"Workshop Detail by Year.xls");
                     FileOutputStream fileOutputStream=new FileOutputStream(file);
                     workshop_detail_by_year.write(fileOutputStream);
                     progressDialog.dismiss();

                     Toast.makeText(Teacher_ListActivity.this, "Workshop Detail Export", Toast.LENGTH_SHORT).show();
                 }
                 catch (Exception ex)
                 {
                     Toast.makeText(Teacher_ListActivity.this, ex.toString(), Toast.LENGTH_SHORT).show();
                 }
             }
             @Override
             public void onCancelled(DatabaseError databaseError) {

             }
         };
         rootref.addListenerForSingleValueEvent(workshop_detail_by_year);

     }



    /*
    *================================================================================================================
    *      Publication Export
    * ===========================================================================================================
    * */


     //Export All Publication Detail

     public void export_all_publication_data()
     {

         final HSSFWorkbook publication_detail=new HSSFWorkbook();
         final HSSFSheet Publication_sheet=publication_detail.createSheet("Publication_deatail");
         HSSFRow header=Publication_sheet.createRow(0);

         header.createCell(0).setCellValue("Name of Faculty");
         header.createCell(1).setCellValue("Name Of Author");
         header.createCell(2).setCellValue("Publication Type");
         header.createCell(3).setCellValue("Title Of Paper");
         header.createCell(4).setCellValue("Name Of conference/Journal");
         header.createCell(5).setCellValue("Date Of Publication");
         header.createCell(6).setCellValue("DOI/ISSN/ISNB");
         header.createCell(7).setCellValue("Academic Year");

         Publication_sheet.setColumnWidth(0,100*75);
         Publication_sheet.setColumnWidth(1,100*100);
         Publication_sheet.setColumnWidth(2,100*75);
         Publication_sheet.setColumnWidth(3,100*75);
         Publication_sheet.setColumnWidth(4,100*75);
         Publication_sheet.setColumnWidth(5,100*50);
         Publication_sheet.setColumnWidth(6,100*75);
         Publication_sheet.setColumnWidth(7,100*50);

         final ProgressDialog progressDialog=new ProgressDialog(Teacher_ListActivity.this);
         progressDialog.setTitle("Data Exporting");
         progressDialog.setMessage("Exporting...");
         progressDialog.show();
         final DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference();
         DatabaseReference rootref=databaseReference.child("Publication_Detail");
         ValueEventListener publication_deatil=new ValueEventListener() {
             @Override
             public void onDataChange(DataSnapshot dataSnapshot) {
                 int index=1;
                 for(DataSnapshot ds:dataSnapshot.getChildren())
                 {
                     if(ds!=null)
                     {
                         for(DataSnapshot child:ds.getChildren())
                         {
                             PublicationModel publicationModel=child.getValue(PublicationModel.class);

                             HSSFRow row=Publication_sheet.createRow(index);

                             row.createCell(0).setCellValue(publicationModel.getName_of_faculy());
                             row.createCell(1).setCellValue(publicationModel.getName_of_author());
                             row.createCell(2).setCellValue(publicationModel.getType_of_publication());
                             row.createCell(3).setCellValue(publicationModel.getTitle_of_paper());
                             row.createCell(4).setCellValue(publicationModel.getName_of_conference());
                             row.createCell(5).setCellValue(publicationModel.getDate_of_publication());
                             row.createCell(6).setCellValue(publicationModel.getDOI());
                             row.createCell(7).setCellValue(publicationModel.getYear_of_publication());

                             index++;
                         }
                     }
                 }

                 try {
                     Context context=getApplicationContext();
                     File file=new File(context.getExternalFilesDir(null),"Publication Detail.xls");
                     FileOutputStream fileOutputStream=new FileOutputStream(file);
                     publication_detail.write(fileOutputStream);
                     Toast.makeText(context, "Publication Detail Export", Toast.LENGTH_SHORT).show();
                     progressDialog.dismiss();
                 } catch (FileNotFoundException e) {
                     e.printStackTrace();
                 } catch (IOException e) {
                     e.printStackTrace();
                 }
             }
             @Override
             public void onCancelled(DatabaseError databaseError) {

             }
         };
         rootref.addListenerForSingleValueEvent(publication_deatil);
     }


    /*==============================================================================================
    * Export publication By Academic
    * ==============================================================================================*/


    public void export_publication_by_academic(final String academic_year)
    {

        final HSSFWorkbook publication_detail=new HSSFWorkbook();
        final HSSFSheet Publication_sheet_by_year=publication_detail.createSheet("Publication_deatail");
        HSSFRow header=Publication_sheet_by_year.createRow(0);

        header.createCell(0).setCellValue("Name of Faculty");
        header.createCell(1).setCellValue("Name Of Author");
        header.createCell(2).setCellValue("Publication Type");
        header.createCell(3).setCellValue("Title Of Paper");
        header.createCell(4).setCellValue("Name Of conference/Journal");
        header.createCell(5).setCellValue("Date Of Publication");
        header.createCell(6).setCellValue("DOI/ISSN/ISNB");
        header.createCell(7).setCellValue("Academic Year");

        Publication_sheet_by_year.setColumnWidth(0,100*75);
        Publication_sheet_by_year.setColumnWidth(1,100*100);
        Publication_sheet_by_year.setColumnWidth(2,100*75);
        Publication_sheet_by_year.setColumnWidth(3,100*75);
        Publication_sheet_by_year.setColumnWidth(4,100*75);
        Publication_sheet_by_year.setColumnWidth(5,100*50);
        Publication_sheet_by_year.setColumnWidth(6,100*75);
        Publication_sheet_by_year.setColumnWidth(7,100*50);

        final ProgressDialog progressDialog=new ProgressDialog(Teacher_ListActivity.this);
        progressDialog.setTitle("Data Exporting");
        progressDialog.setMessage("Exporting...");
        progressDialog.show();
        final DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference();
        DatabaseReference rootref=databaseReference.child("Publication_Detail");
        ValueEventListener publication_deatil=new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int index=1;
                for(DataSnapshot ds:dataSnapshot.getChildren())
                {
                    if(ds!=null)
                    {
                        for(DataSnapshot child:ds.getChildren())
                        {
                            PublicationModel publicationModel=child.getValue(PublicationModel.class);

                            if(publicationModel.getYear_of_publication().equals(academic_year)) {

                                HSSFRow row = Publication_sheet_by_year.createRow(index);

                                row.createCell(0).setCellValue(publicationModel.getName_of_faculy());
                                row.createCell(1).setCellValue(publicationModel.getName_of_author());
                                row.createCell(2).setCellValue(publicationModel.getType_of_publication());
                                row.createCell(3).setCellValue(publicationModel.getTitle_of_paper());
                                row.createCell(4).setCellValue(publicationModel.getName_of_conference());
                                row.createCell(5).setCellValue(publicationModel.getDate_of_publication());
                                row.createCell(6).setCellValue(publicationModel.getDOI());
                                row.createCell(7).setCellValue(publicationModel.getYear_of_publication());

                                index++;
                            }
                        }
                    }
                }

                try {
                    Context context=getApplicationContext();
                    File file=new File(context.getExternalFilesDir(null),"publication Detail by Year.xls");
                    FileOutputStream fileOutputStream=new FileOutputStream(file);
                    publication_detail.write(fileOutputStream);
                    Toast.makeText(context, "Publication Detail Export", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        rootref.addListenerForSingleValueEvent(publication_deatil);


    }






     /*==============================================================================================
     * Change Password Funcation Implementation
     * =============================================================================================*/

    public void Change_Password()
     {

         container=new BottomSheetDialog(Teacher_ListActivity.this);
         container.setContentView(R.layout.change_password_layout);
         container.show();

         final EditText email=(EditText) container.findViewById(R.id.id_change_password_email);
         final EditText password=(EditText) container.findViewById(R.id.id_change_password_old_password);
         final EditText new_pass=(EditText) container.findViewById(R.id.id_change_password_new_password);
         password.setTransformationMethod(new PasswordTransformationMethod());
         new_pass.setTransformationMethod(new PasswordTransformationMethod());
         Button change_password=(Button)container.findViewById(R.id.id_change_password_submit_btn);
         change_password.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {

                 if(TextUtils.isEmpty(email.getText().toString()))
                 {
                     Snackbar.make(container.getWindow().getDecorView(),"Enter Email ID",Snackbar.LENGTH_SHORT).show();
                     return;
                 }
                 else if(TextUtils.isEmpty(password.getText().toString()))
                 {
                     Snackbar.make(container.getWindow().getDecorView(),"Enter Password",Snackbar.LENGTH_SHORT).show();
                     return;
                 }
                 else if (TextUtils.isEmpty(new_pass.getText().toString()))
                 {
                     Snackbar.make(container.getWindow().getDecorView(),"Enter New Password",Snackbar.LENGTH_SHORT).show();
                     return;
                 }

                 current_user=FirebaseAuth.getInstance().getCurrentUser();
                 AuthCredential credential= EmailAuthProvider.getCredential(email.getText().toString(),password.getText().toString());

                 current_user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                     @Override
                     public void onComplete(@NonNull Task<Void> task) {

                         if(task.isSuccessful())
                         {

                             current_user.updatePassword(new_pass.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                 @Override
                                 public void onComplete(@NonNull Task<Void> task) {
                                     if(task.isSuccessful())
                                     {
                                         Toast.makeText(Teacher_ListActivity.this, "Password Changed", Toast.LENGTH_SHORT).show();
                                     }
                                 }
                             });
                         }
                         else
                         {
                             Toast.makeText(Teacher_ListActivity.this, "Invalid Email Password", Toast.LENGTH_SHORT).show();
                         }
                     }
                 }).addOnFailureListener(new OnFailureListener() {
                     @Override
                     public void onFailure(@NonNull Exception e) {

                     }
                 });
             }
         });
     }

    /*--------------------------------------------------------------------------------------------------
    *                   External Storage Permission
    * --------------------------------------------------------------------------------------------------*/
    public static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    public static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    /*=====================================================================================================
    *                   Check Internet Connection
    * ====================================================================================================*/

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
}