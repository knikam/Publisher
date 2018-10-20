package com.swami.kalpesh.publisher.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.swami.kalpesh.publisher.Model.TeacherInfoModel;
import com.swami.kalpesh.publisher.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class User_profileActivity extends AppCompatActivity {

    EditText name,email,qualification,designation,contact_no;
    Button update_btn;
    ImageView edit_btn;
    CircleImageView profile_pic;

    Boolean flag=false;
    Uri imageuri;

    String user;
    TeacherInfoModel infoModel;

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("User Profile");

        name=findViewById(R.id.id_profile_name);
        email=findViewById(R.id.id_profile_email);
        qualification=findViewById(R.id.id_profile_qualification);
        designation=findViewById(R.id.id_profile_Designation);
        contact_no=findViewById(R.id.id_profile_contact_no);

        profile_pic=findViewById(R.id.id_profile_image);
        update_btn=findViewById(R.id.id_profile_update_btn);
        edit_btn=findViewById(R.id.id_profile_edit_button);

        name.setEnabled(false);
        email.setEnabled(false);
        qualification.setEnabled(false);
        designation.setEnabled(false);
        contact_no.setEnabled(false);
        update_btn.setEnabled(false);

        edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                flag=true;
                name.setEnabled(true);
                email.setEnabled(false);
                qualification.setEnabled(true);
                designation.setEnabled(true);
                contact_no.setEnabled(true);
                update_btn.setEnabled(true);
            }
        });

        update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final ProgressDialog progressDialog=new ProgressDialog(User_profileActivity.this);
                progressDialog.setMessage("Updating...");
                progressDialog.show();

                if(imageuri!=null)
                {
                    user=email.getText().toString().toLowerCase();
                    int index=user.indexOf("@");
                    final String childkey=user.substring(0,index);

                    databaseReference=FirebaseDatabase.getInstance().getReference("Active_users").child(childkey);
                    StorageReference storageReference= FirebaseStorage.getInstance().getReference("User").child(contact_no.getText().toString());
                    storageReference.putFile(imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            TeacherInfoModel teacherInfoModel=new TeacherInfoModel();
                            teacherInfoModel.setName(name.getText().toString());
                            teacherInfoModel.setEmail(email.getText().toString());
                            teacherInfoModel.setQualification(qualification.getText().toString());
                            teacherInfoModel.setDesignation(designation.getText().toString());
                            teacherInfoModel.setContact_no(contact_no.getText().toString());
                            teacherInfoModel.setImage(taskSnapshot.getDownloadUrl().toString());
                            teacherInfoModel.setApproval("Approved");
                            databaseReference.setValue(teacherInfoModel);
                            progressDialog.dismiss();
                            Snackbar.make(findViewById(R.id.id_profile_layout),"Profile Update", BaseTransientBottomBar.LENGTH_SHORT)
                                    .show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Snackbar.make(findViewById(R.id.id_profile_layout),"Fail Profile Update", BaseTransientBottomBar.LENGTH_SHORT)
                                    .show();
                        }
                    }) ;
                }


                else
                {
                    user=email.getText().toString().toLowerCase();
                    int index=user.indexOf("@");
                    final String childkey=user.substring(0,index);
                    databaseReference=FirebaseDatabase.getInstance().getReference("Active_users").child(childkey);
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            TeacherInfoModel teacherInfoModel=new TeacherInfoModel();
                            teacherInfoModel.setName(name.getText().toString());
                            teacherInfoModel.setEmail(email.getText().toString());
                            teacherInfoModel.setQualification(qualification.getText().toString());
                            teacherInfoModel.setDesignation(designation.getText().toString());
                            teacherInfoModel.setContact_no(contact_no.getText().toString());
                            teacherInfoModel.setImage(infoModel.getImage());
                            teacherInfoModel.setApproval("Approved");

                            databaseReference.setValue(teacherInfoModel);
                            progressDialog.dismiss();
                            Snackbar.make(findViewById(R.id.id_profile_layout),"Profile Update", BaseTransientBottomBar.LENGTH_SHORT)
                                    .show();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            //
                        }
                    });
                }
            }
        });



        profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(flag)
                {
                    Intent intent=new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent,"Select Image"),1);
                }
            }
        });



        Get_user_data();
    }

    public void Get_user_data() {

            final ProgressDialog progressDialog=new ProgressDialog(User_profileActivity.this);
            progressDialog.setTitle("getting Profile");
            progressDialog.setMessage("Please Wait");
            progressDialog.setCancelable(false);
            progressDialog.show();

            String user = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            user = user.toLowerCase();
            final int index = user.indexOf("@");
            final String childkey = user.substring(0, index);

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Active_users").child(childkey);
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    infoModel = dataSnapshot.getValue(TeacherInfoModel.class);
                    name.setText(infoModel.getName());
                    email.setText(infoModel.getEmail());
                    qualification.setText(infoModel.getQualification());
                    designation.setText(infoModel.getDesignation());
                    contact_no.setText(infoModel.getContact_no());
                    Glide.with(getApplicationContext()).load(infoModel.getImage()).error(R.drawable.placeholder).into(profile_pic);
                    progressDialog.dismiss();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1 && resultCode==RESULT_OK){
            imageuri=data.getData();
            profile_pic.setImageURI(imageuri);
        }
    }

    private String getPathFromURI(Uri imageuri) {

        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(imageuri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
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
